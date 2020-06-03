/*
 * Copyright 2020 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.pyamsoft.pydroid.util

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.annotation.CheckResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@CheckResult
suspend fun SharedPreferences.onChange(
    key: String,
    onChange: suspend () -> Unit
): PreferenceListener {
    val preferences = this
    return withContext(context = Dispatchers.Default) {
        val listener = OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == key) {
                launch(context = Dispatchers.Default) { onChange() }
            }
        }

        return@withContext PreferenceListenerImpl(preferences, listener)
    }
}

private class PreferenceListenerImpl internal constructor(
    private val preferences: SharedPreferences,
    listener: OnSharedPreferenceChangeListener
) : PreferenceListener {

    private var listener: OnSharedPreferenceChangeListener? = listener

    init {
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun cancel() {
        listener?.let { preferences.unregisterOnSharedPreferenceChangeListener(it) }
        listener = null
    }
}

interface PreferenceListener {

    fun cancel()
}
