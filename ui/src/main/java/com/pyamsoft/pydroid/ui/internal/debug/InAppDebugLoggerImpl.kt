/*
 * Copyright 2024 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
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
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.debug.InAppDebugLogger
import com.pyamsoft.pydroid.ui.debug.InAppDebugStatus
import com.pyamsoft.pydroid.ui.internal.debug.InAppDebugLogLine.Level
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph.ApplicationScope
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext

/** A logger which captures internal log messages and publishes them on a bus to an in-app view */
internal class InAppDebugLoggerImpl
internal constructor(
    application: Application,
) : InAppDebugLogger {

  // Inject target
  internal var logLines: MutableStateFlow<List<InAppDebugLogLine>>? = null
  internal var status: InAppDebugStatus? = null

  private var heldApplication: Application? = application
  private val isLoggingEnabled = MutableStateFlow(false)

  @CheckResult
  @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
  private fun scope(): CoroutineScope {
    return CoroutineScope(
        context =
            SupervisorJob() +
                newSingleThreadContext(this::class.java.name) +
                CoroutineName(this::class.java.name),
    )
  }

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
    if (logLines == null || status == null) {
      // Safely attempt to inject this
      getPYDroid(application)?.injector()?.inject(this)

      // If we are injected, let's start our listen bus
      status?.also { prefs ->
        // Don't hold onto Application.Context past what we need it for
        heldApplication = null

        // If we are here, we are injected
        val l = logLines.requireNotNull()

        // Should only launch once if we are injected correctly
        prefs.listenForInAppDebuggingEnabled().also { f ->
          scope().launch {
            f.collect { enabled ->
              val newEnabled = isLoggingEnabled.updateAndGet { enabled }

              // Clear log bus if disabled
              if (!newEnabled) {
                l.update { emptyList() }
              }
            }
          }
        }
      }
    }
  }

  private fun publishLog(
      level: Level,
      line: String,
      throwable: Throwable?,
  ) {
    // We may not be around yet, since we may not be injected yet
    logLines?.also { b ->
      // If logging is enabled, this can be expensive.
      // Double check here just to avoid any extra work
      if (isLoggingEnabled.value) {
        // Record the timestamp before the coroutine launched as this is immediate
        val timestamp = System.nanoTime()

        b.update { lines ->
          if (isLoggingEnabled.value) {
            // This can potentially be a huge list that can affect performance.
            lines +
                InAppDebugLogLine(
                    timestamp = timestamp,
                    level = level,
                    line = line,
                    throwable = throwable,
                )
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
      Log.ASSERT,
      Log.ERROR -> log(Level.ERROR, t, message, throwable)
      Log.WARN -> log(Level.WARNING, t, message, throwable)
      else -> log(Level.DEBUG, t, message, throwable)
    }
  }
}
