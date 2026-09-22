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

package de.yisrime.lrcbar.hook

import io.github.kyuubiran.ezxhelper.core.EzXReflection
import io.github.kyuubiran.ezxhelper.xposed.EzXposed
import io.github.kyuubiran.ezxhelper.xposed.EzXposed.moduleRes
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam
import de.yisrime.lrcbar.BuildConfig
import de.yisrime.lrcbar.R
import de.yisrime.lrcbar.config.ConfigStore
import de.yisrime.lrcbar.config.XposedOwnSP.config
import de.yisrime.lrcbar.hook.module.SystemUILyric
import de.yisrime.lrcbar.hook.module.SystemUITest
import de.yisrime.lrcbar.tools.LogTools
import de.yisrime.lrcbar.tools.LogTools.log
import java.util.Locale

class MainHook : XposedModule() {
    override fun onModuleLoaded(param: ModuleLoadedParam) {
        EzXposed.initOnModuleLoaded(this, param)
        EzXposed.initModuleResources()
    }

    override fun onPackageLoaded(param: PackageLoadedParam) {
        EzXposed.initOnPackageLoaded(param)
        EzXReflection.init(param.defaultClassLoader)
        attachPreferences()
        LogTools.init(config.outLog)
        when (param.packageName) {
            "com.android.systemui" -> {
                if (!config.masterSwitch) {
                    moduleRes.getString(R.string.master_off).log()
                    return
                }
                "${BuildConfig.APPLICATION_ID} - ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE}[${Locale.getDefault().language}] *${BuildConfig.BUILD_TYPE})".log()
                if (config.testMode) {
                    moduleRes.getString(R.string.hook_page).log()
                    initHooks(SystemUITest())
                } else {
                    moduleRes.getString(R.string.lyric_mode).log()
                    try {
                        initHooks(SystemUILyric())
                    } catch (t: Throwable) {
                        t.log()
                    }
                }
            }
        }
    }

    private fun attachPreferences() {
        runCatching {
            ConfigStore.attach(getRemotePreferences(ConfigStore.GROUP), false)
        }.onFailure { it.log() }
    }

    private fun initHooks(vararg hook: BaseHook) {
        hook.forEach {
            try {
                if (it.isInit) return
                it.init()
                it.isInit = true
                "${moduleRes.getString(R.string.hook_succeeded)}:${it.javaClass.simpleName}".log()
            } catch (e: Exception) {
                "${moduleRes.getString(R.string.hook_failed)}:${it.javaClass.simpleName}".log()
                e.log()
            }
        }
    }
}
