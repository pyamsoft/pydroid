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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber

@CheckResult
fun SharedPreferences.onChange(
    key: String,
    onChange: suspend () -> Unit
): PreferenceListener {
    val preferences = this
    val listener = object : ScopedPreferenceChangeListener(key) {

        override suspend fun onChange() {
            Timber.d("Fire on change: $key")
            onChange()
        }
    }

    return PreferenceListenerImpl(preferences, listener)
}

private abstract class ScopedPreferenceChangeListener(private val watchKey: String) :
    OnSharedPreferenceChangeListener {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    final override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        key: String
    ) {
        Timber.d("On sharedpreferences changed: $key")
        if (watchKey == key) {
            Timber.d("Watched key changed: $key")
            scope.launch(context = Dispatchers.Default) {
                onChange()
            }
        }
    }

    protected abstract suspend fun onChange()

    fun cancel() {
        scope.cancel()
    }
}

private class PreferenceListenerImpl internal constructor(
    private val preferences: SharedPreferences,
    listener: ScopedPreferenceChangeListener
) : PreferenceListener {

    private var listener: ScopedPreferenceChangeListener? = listener

    init {
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun cancel() {
        listener?.let { l ->
            preferences.unregisterOnSharedPreferenceChangeListener(l)
            l.cancel()
        }
        listener = null
    }
}

interface PreferenceListener {

    fun cancel()
}
