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

package de.yisrime.lycbar.config

import android.content.SharedPreferences

object ConfigStore {
    const val GROUP = "COMPOSE_CONFIG"

    @Volatile
    private var prefs: SharedPreferences? = null

    @Volatile
    private var writable = false

    val source: SharedPreferences?
        get() = prefs

    val isEmpty: Boolean
        get() = prefs?.all?.isEmpty() != false

    fun attach(source: SharedPreferences, writable: Boolean) {
        prefs = source
        this.writable = writable
    }

    fun detach() {
        prefs = null
        writable = false
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> opt(key: String, default: T): T {
        val sp = prefs ?: return default
        return when (default) {
            is String -> sp.getString(key, default) as T
            is Int -> sp.getInt(key, default) as T
            is Long -> sp.getLong(key, default) as T
            is Boolean -> sp.getBoolean(key, default) as T
            is Float -> sp.getFloat(key, default) as T
            is Double -> sp.getFloat(key, default.toFloat())?.toDouble() as T
            else -> default
        }
    }

    fun put(key: String, value: Any?) {
        val editor = editor() ?: return
        editor.putTyped(key, value)
        editor.apply()
    }

    fun putAll(entries: Map<String, *>) {
        val editor = editor() ?: return
        entries.forEach { (key, value) -> editor.putTyped(key, value) }
        editor.apply()
    }

    fun clearAll() {
        editor()?.clear()?.apply()
    }

    private fun editor(): SharedPreferences.Editor? {
        val sp = prefs ?: return null
        if (!writable) return null
        return sp.edit()
    }

    private fun SharedPreferences.Editor.putTyped(key: String, value: Any?) {
        when (value) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Boolean -> putBoolean(key, value)
            is Float -> putFloat(key, value)
        }
    }
}
