/*
 * StatusBarLyric
 * Copyright (C) 2021-2022 fkj@fkj233.cn
 * Copyright (C) 2026 YisRime
 * https://github.com/Block-Network/StatusBarLyric
 *
 * Portions Copyright (C) 2026 Yis_Rime
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

package de.yisrime.lycbar

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper
import de.yisrime.lycbar.config.ConfigStore

class LyricApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        XposedServiceHelper.registerListener(object : XposedServiceHelper.OnServiceListener {
            override fun onServiceBind(service: XposedService) {
                runCatching {
                    ConfigStore.attach(service.getRemotePreferences(ConfigStore.GROUP), true)
                    importLegacyPreferences()
                }.onFailure { Log.e(TAG, "框架服务绑定失败", it) }
                ready.value = ConfigStore.source != null
            }

            override fun onServiceDied(service: XposedService) {
                ConfigStore.detach()
                ready.value = false
            }
        })
    }

    private fun importLegacyPreferences() {
        val name = ConfigStore.GROUP
        val direct = createDeviceProtectedStorageContext()
        val legacy = listOf(
            direct.getSharedPreferences(name, Context.MODE_PRIVATE),
            getSharedPreferences(name, Context.MODE_PRIVATE),
        )
        if (ConfigStore.isEmpty) {
            legacy.firstOrNull { !it.all.isNullOrEmpty() }?.let { ConfigStore.putAll(it.all) }
        }
        direct.deleteSharedPreferences(name)
        deleteSharedPreferences(name)
    }

    companion object {
        private const val TAG = "StatusBarLyric"

        val ready = mutableStateOf(false)
    }
}
