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

package com.pyamsoft.pydroid.autopsy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.CheckResult
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.theme.PYDroidTheme
import kotlin.system.exitProcess

/**
 * The screen that will show up on device when a crash occurs
 *
 * Will also log the crash to logcat
 */
internal class CrashActivity internal constructor() : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Hide ActionBar
    actionBar?.hide()

    val launchIntent = intent
    val threadName = launchIntent.getStringExtra(KEY_THREAD_NAME).requireNotNull()
    val throwableName = launchIntent.getStringExtra(KEY_THROWABLE).requireNotNull()
    val throwableMessage = launchIntent.getStringExtra(KEY_MESSAGE).requireNotNull()
    val stackTrace = launchIntent.getStringExtra(KEY_TRACE).requireNotNull()

    setContent {
      PYDroidTheme {
        SystemBars(
            isDarkMode = isSystemInDarkTheme(),
        )
        CrashScreen(
            modifier = Modifier.fillMaxSize(),
            threadName = threadName,
            throwableName = throwableName,
            throwableMessage = throwableMessage,
            stackTrace = stackTrace,
        )
      }
    }
  }

  override fun onStop() {
    super.onStop()
    finish()
  }

  override fun onDestroy() {
    super.onDestroy()
    exitProcess(0)
  }

  companion object {

    private const val KEY_THREAD_NAME = "key_thread_name"
    private const val KEY_THROWABLE = "key_throwable"
    private const val KEY_MESSAGE = "key_message"
    private const val KEY_TRACE = "key_trace"

    @CheckResult
    @JvmStatic
    internal fun newIntent(context: Context, threadName: String, throwable: Throwable): Intent {
      return Intent(context.applicationContext, CrashActivity::class.java).apply {
        putExtra(KEY_THREAD_NAME, threadName)
        putExtra(KEY_THROWABLE, throwable::class.java.simpleName)
        putExtra(KEY_MESSAGE, throwable.message.orEmpty())
        putExtra(KEY_TRACE, throwable.stackTraceToString())
        flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NO_ANIMATION
      }
    }
  }
}
