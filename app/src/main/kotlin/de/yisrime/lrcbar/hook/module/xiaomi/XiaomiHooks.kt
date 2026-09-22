/*
 * StatusBarLyric
 * Copyright (C) 2021-2022 fkj@fkj233.cn
 * Copyright (C) 2026 YisRime
 * https://github.com/Block-Network/StatusBarLyric
 *
 * This software is free opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as
 * published by Block-Network contributors.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/Block-Network/StatusBarLyric/blob/main/LICENSE>.
 */

package de.yisrime.lrcbar.hook.module.xiaomi

import android.os.Build
import android.view.View
import android.widget.TextView
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil.loadClassOrNull
import io.github.kyuubiran.ezxhelper.xposed.EzXposed.moduleRes
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook
import io.github.kyuubiran.ezxhelper.core.helper.ObjectHelper.`-Static`.objectHelper
import io.github.kyuubiran.ezxhelper.core.finder.ConstructorFinder.`-Static`.constructorFinder
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import de.yisrime.lrcbar.R
import de.yisrime.lrcbar.config.XposedOwnSP.config
import de.yisrime.lrcbar.hook.module.SystemUILyric
import de.yisrime.lrcbar.tools.LogTools.log
import de.yisrime.lrcbar.tools.Tools.callMethod
import de.yisrime.lrcbar.tools.Tools.existMethod
import de.yisrime.lrcbar.tools.Tools.getObjectField
import de.yisrime.lrcbar.tools.Tools.ifNotNull
import de.yisrime.lrcbar.tools.Tools.isNotNull
import de.yisrime.lrcbar.tools.Tools.isPad
import de.yisrime.lrcbar.tools.Tools.setObjectField
import de.yisrime.lrcbar.tools.XiaomiUtils.isXiaomi
import java.lang.ref.WeakReference

class XiaomiHooks {
    companion object {
        private var notificationBigTimeRef: WeakReference<View>? = null
        private var miuiNetworkSpeedViewRef: WeakReference<TextView>? = null
        private var padClockViewRef: WeakReference<View>? = null
        private var carrierLabelRef: WeakReference<View>? = null

        fun getNotificationBigTime(): View? = notificationBigTimeRef?.get()
        private fun setNotificationBigTime(view: View?) {
            notificationBigTimeRef = if (view.isNotNull()) WeakReference(view) else null
        }

        fun getMiuiNetworkSpeedView(): TextView? = miuiNetworkSpeedViewRef?.get()
        private fun setMiuiNetworkSpeedView(view: TextView?) {
            miuiNetworkSpeedViewRef = if (view.isNotNull()) WeakReference(view) else null
        }

        fun getPadClockView(): View? = padClockViewRef?.get()
        private fun setPadClockView(view: View?) {
            padClockViewRef = if (view.isNotNull()) WeakReference(view) else null
        }

        fun getCarrierLabel(): View? = carrierLabelRef?.get()
        private fun setCarrierLabel(view: View?) {
            carrierLabelRef = if (view.isNotNull()) WeakReference(view) else null
        }

        fun init(systemUILyric: SystemUILyric) {
            if (!isXiaomi) return

            // 处理焦点通知
            FocusNotifyController.init(systemUILyric)

            // 处理通知中心时间
            loadClassOrNull("com.android.systemui.controlcenter.shade.NotificationHeaderExpandController\$notificationCallback$1").isNotNull {
                it.methodFinder().filterByName("onExpansionChanged").filterFinal().single().createHook {
                    before { hook ->
                        if (systemUILyric.isMusicPlaying && !systemUILyric.isHiding && config.hideTime) {
                            val notificationHeaderExpandController = hook.thisObject.getObjectField("this$0")
                            notificationHeaderExpandController?.setObjectField("bigTimeTranslationY", 0)
                            notificationHeaderExpandController?.setObjectField("notificationTranslationX", 0)

                            val bigTimeView = notificationHeaderExpandController
                                ?.getObjectField("headerController")?.callMethod("get")
                                ?.getObjectField("notificationBigTime") as? View

                            setNotificationBigTime(bigTimeView)

                            val f = hook.args[0] as Float
                            if (f < 0.75f) getNotificationBigTime()?.visibility = View.GONE
                            else getNotificationBigTime()?.visibility = View.VISIBLE
                        }
                    }
                }
            }

            // 隐藏小米网络速度
            if (config.mMiuiHideNetworkSpeed) {
                moduleRes.getString(R.string.miui_hide_network_speed).log()
                loadClassOrNull("com.android.systemui.statusbar.views.NetworkSpeedView").isNotNull {
                    it.constructorFinder().single().createHook {
                        after { hookParam ->
                            setMiuiNetworkSpeedView(hookParam.thisObject as? TextView)
                        }
                    }
                    it.methodFinder().filterByName("setVisibilityByController").single()
                        .createHook {
                            before { hookParam ->
                                if (systemUILyric.isMusicPlaying) hookParam.args[0] = false
                            }
                        }
                }
            }

            // 小米平板优化
            if (config.mMiuiPadOptimize) {
                loadClassOrNull("com.android.systemui.SystemUIApplication").isNotNull { clazz ->
                    clazz.methodFinder().filterByName("onCreate").single().createHook {
                        after {
                            if (isPad) {
                                loadClassOrNull("com.android.systemui.statusbar.phone.MiuiCollapsedStatusBarFragment").isNotNull {
                                    if (it.existMethod("initMiuiViewsOnViewCreated")) {
                                        it.methodFinder().filterByName("initMiuiViewsOnViewCreated").single()
                                    } else {
                                        it.methodFinder().filterByName("onViewCreated").single()
                                    }.let { method ->
                                        method.createHook {
                                            after { hookParam ->
                                                hookParam.thisObject.objectHelper {
                                                    setPadClockView((this.getObjectOrNull("mPadClockView") as View?))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 隐藏锁屏状态栏运营商
            if (config.hideCarrier && Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
                moduleRes.getString(R.string.hide_carrier).log()
                loadClassOrNull("com.android.systemui.statusbar.phone.KeyguardStatusBarView").isNotNull {
                    it.methodFinder().filterByName("onFinishInflate").singleOrNull()
                        .ifNotNull { method ->
                            method.createHook {
                                after { hookParam ->
                                    kotlin.runCatching {
                                        val clazz = hookParam.thisObject::class.java
                                        if (clazz.simpleName == "KeyguardStatusBarView") {
                                            hookParam.thisObject.objectHelper {
                                                setCarrierLabel((this.getObjectOrNull("mCarrierLabel") as View?))
                                            }
                                        } else {
                                            setCarrierLabel(clazz.superclass.getField("mCarrierLabel").get(hookParam.thisObject) as View)
                                        }
                                    }.onFailure { throwable -> "Hook carrier error: $throwable".log() }
                                }
                            }
                        }
                }
            }
        }
    }
}