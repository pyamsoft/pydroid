/*
 * Copyright 2023 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.internal.debug

import android.app.Application
import android.util.Log
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.debug.InAppDebugLogger
import com.pyamsoft.pydroid.ui.internal.debug.LogLine.Level
import com.pyamsoft.pydroid.ui.internal.debug.LogLine.Level.DEBUG
import com.pyamsoft.pydroid.ui.internal.debug.LogLine.Level.ERROR
import com.pyamsoft.pydroid.ui.internal.debug.LogLine.Level.WARNING
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph.ApplicationScope
import kotlin.LazyThreadSafetyMode.NONE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** A logger which captures internal log messages and publishes them on a bus to an in-app view */
internal class InAppDebugLoggerImpl
internal constructor(
    application: Application,
) : InAppDebugLogger {

  // Inject target
  internal var bus: MutableStateFlow<List<LogLine>>? = null
  internal var preferences: DebugPreferences? = null

  private var heldApplication: Application? = application
  private var isLoggingEnabled = false

  // Coroutines
  private var preferenceListenJob: Job? = null
  private val scope by lazy(NONE) { MainScope() }

  @CheckResult
  private fun getPYDroid(application: Application): PYDroid? {
    return try {
      ApplicationScope.retrieve(application)
    } catch (e: Throwable) {
      // PYDroid isn't around yet, either not initialized (maybe we are logging before
      // installPYDroid) or never used
      null
    }
  }

  private fun injectPYDroid(application: Application) {
    if (bus == null || preferences == null) {
      // Safely attempt to inject this
      getPYDroid(application)?.injector()?.inject(this)

      // If we are injected, let's start our listen bus
      preferences?.also { prefs ->
        // Don't hold onto Application.Context past what we need it for
        heldApplication = null

        preferenceListenJob?.cancel()
        preferenceListenJob =
            scope.launch(context = Dispatchers.Main) {
              prefs.listenForInAppDebuggingEnabled().collectLatest { isLoggingEnabled = it }
            }
      }
    }
  }

  private fun publishLog(
      level: Level,
      line: String,
      throwable: Throwable?,
  ) {
    bus?.also { b ->
      scope.launch(context = Dispatchers.IO) {
        b.update { lines ->
          if (isLoggingEnabled) {
            lines + LogLine(level, line, throwable)
          } else {
            emptyList()
          }
        }
      }
    }
  }

  private fun log(
      level: Level,
      tag: String,
      message: String,
      throwable: Throwable?,
  ) {
    heldApplication?.also { injectPYDroid(it) }
    val logTag = if (tag.isBlank()) "" else "$tag "
    publishLog(level, "${logTag}$message", throwable)
  }

  override fun log(
      priority: Int,
      tag: String?,
      message: String,
      throwable: Throwable?,
  ) {
    val t = tag.orEmpty()
    when (priority) {
      Log.ASSERT -> Log.wtf(t, message)
      Log.ERROR -> log(ERROR, t, message, throwable)
      Log.WARN -> log(WARNING, t, message, throwable)
      else -> log(DEBUG, t, message, throwable)
    }
  }
}
