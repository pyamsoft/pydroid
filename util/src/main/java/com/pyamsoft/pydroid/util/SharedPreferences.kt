/*
 * Copyright 2023 pyamsoft
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
 */

package com.pyamsoft.pydroid.util

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.annotation.CheckResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@CheckResult
private inline fun <R : Any, T : Any> SharedPreferences.preferenceFlow(
    key: String,
    defaultValue: T,
    crossinline getter: SharedPreferences.(String, T) -> R,
): Flow<R> {
  val self = this
  return callbackFlow {
    val listener = OnSharedPreferenceChangeListener { prefs, changedKey: String? ->
      // changedKey can be NULL when preferences are cleared, see documentation
      if (changedKey === key) {
        // Block the listener
        trySendBlocking(prefs.getter(key, defaultValue))
      }
    }

    self.registerOnSharedPreferenceChangeListener(listener)

    // Block the flow while this is sending
    trySendBlocking(self.getter(key, defaultValue))

    awaitClose { self.unregisterOnSharedPreferenceChangeListener(listener) }
  }
}

/** Watch a SharedPreference Int for changes as a Flow */
@CheckResult
public fun SharedPreferences.intFlow(key: String, defaultValue: Int): Flow<Int> {
  return this.preferenceFlow(key, defaultValue) { k, v -> getInt(k, v) }
}

/** Watch a SharedPreference Boolean for changes as a Flow */
@CheckResult
public fun SharedPreferences.booleanFlow(key: String, defaultValue: Boolean): Flow<Boolean> {
  return this.preferenceFlow(key, defaultValue) { k, v -> getBoolean(k, v) }
}

/** Watch a SharedPreference String for changes as a Flow */
@CheckResult
public fun SharedPreferences.stringFlow(key: String, defaultValue: String): Flow<String> {
  return this.preferenceFlow(key, defaultValue) { k, v -> getString(k, v) ?: defaultValue }
}

/** Watch a SharedPreference Float for changes as a Flow */
@CheckResult
public fun SharedPreferences.floatFlow(key: String, defaultValue: Float): Flow<Float> {
  return this.preferenceFlow(key, defaultValue) { k, v -> getFloat(k, v) }
}

/** Watch a SharedPreference Long for changes as a Flow */
@CheckResult
public fun SharedPreferences.longFlow(key: String, defaultValue: Long): Flow<Long> {
  return this.preferenceFlow(key, defaultValue) { k, v -> getLong(k, v) }
}

/** Watch a SharedPreference String Set for changes as a Flow */
@CheckResult
public fun SharedPreferences.stringSetFlow(
    key: String,
    defaultValue: Set<String>
): Flow<Set<String>> {
  return this.preferenceFlow(key, defaultValue) { k, v ->
    getStringSet(k, v)?.toSet() ?: defaultValue
  }
}
