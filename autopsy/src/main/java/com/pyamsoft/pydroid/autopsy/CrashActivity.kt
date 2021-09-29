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
 */

package com.pyamsoft.pydroid.autopsy

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.annotation.ColorRes
import com.pyamsoft.pydroid.autopsy.databinding.ActivityCrashBinding
import com.pyamsoft.pydroid.core.requireNotNull
import kotlin.system.exitProcess

/**
 * The screen that will show up on device when a crash occurs
 *
 * Will also log the crash to logcat
 */
internal class CrashActivity internal constructor() : Activity() {

  private val logger = Logger.tag(this)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    actionBar?.hide()

    val binding = ActivityCrashBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val backgroundColor = getColor(resources, R.color.crash_background_color, theme)

    window?.apply {
      addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      statusBarColor = backgroundColor
      navigationBarColor = backgroundColor
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
        navigationBarDividerColor = backgroundColor
      }
    }

    val threadName = intent.getStringExtra(KEY_THREAD_NAME).requireNotNull()
    val throwableName = intent.getStringExtra(KEY_THROWABLE).requireNotNull()
    val stackTrace = intent.getSerializableExtra(KEY_TRACE).requireNotNull() as Throwable

    // Set the thread name
    setText(binding.crashThreadName, "Uncaught exception in $threadName thread")

    // Set the throwable name
    setText(binding.crashException, throwableName)

    // Set the message
    stackTrace.message?.also { setText(binding.crashMessage, it) }

    // Allow the stack trace to scroll
    stackTrace.message?.also { setText(binding.crashMessage, it) }

    // Allow the stack trace to scroll
    setText(binding.crashTrace, stackTrace.stackTraceToString())
    binding.crashTrace.apply {
      isVerticalScrollBarEnabled = true
      movementMethod = ScrollingMovementMethod()
    }

    logger.e("===================")
    logger.e(stackTrace, "APPLICATION CRASHED")
    logger.e("===================")
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

    @JvmStatic
    @SuppressLint("SetTextI18n")
    private fun setText(view: TextView, text: String) {
      view.text = text
    }

    @JvmStatic
    private fun getColor(resources: Resources, @ColorRes color: Int, theme: Theme): Int {
      return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        resources.getColor(color, theme)
      } else {
        @Suppress("DEPRECATION") resources.getColor(color)
      }
    }

    private const val KEY_THREAD_NAME = "key_thread_name"
    private const val KEY_THROWABLE = "key_throwable"
    private const val KEY_TRACE = "key_trace"

    @CheckResult
    @JvmStatic
    internal fun newIntent(context: Context, threadName: String, throwable: Throwable): Intent {
      return Intent(context.applicationContext, CrashActivity::class.java).apply {
        putExtra(KEY_THREAD_NAME, threadName)
        putExtra(KEY_THROWABLE, throwable::class.java.simpleName)
        putExtra(KEY_TRACE, throwable)
        flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NO_ANIMATION
      }
    }
  }
}
