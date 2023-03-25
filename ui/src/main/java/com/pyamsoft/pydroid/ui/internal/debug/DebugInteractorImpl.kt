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

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.annotation.CheckResult
import androidx.core.content.getSystemService
import com.pyamsoft.pydroid.core.ThreadEnforcer
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.internal.debug.InAppDebugLogLine.Level.DEBUG
import com.pyamsoft.pydroid.ui.internal.debug.InAppDebugLogLine.Level.ERROR
import com.pyamsoft.pydroid.ui.internal.debug.InAppDebugLogLine.Level.WARNING
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.LazyThreadSafetyMode.NONE

/** Interactor for Debug operations */
internal class DebugInteractorImpl
internal constructor(
    enforcer: ThreadEnforcer,
    private val context: Context,
) : DebugInteractor {

  private val clipboardManager by
      lazy(NONE) {
        enforcer.assertOffMainThread()

        context.applicationContext.getSystemService<ClipboardManager>().requireNotNull()
      }

  @CheckResult
  private fun parseLine(line: InAppDebugLogLine): String {
    val level =
        when (line.level) {
          DEBUG -> "[D]"
          WARNING -> "[W]"
          ERROR -> "[E]"
        }

    val errorMessage = if (line.throwable == null) "" else line.throwable.message.orEmpty()
    return "$level ${line.line} $errorMessage"
  }

  override suspend fun copyInAppDebugMessagesToClipboard(lines: List<InAppDebugLogLine>) =
      withContext(context = Dispatchers.IO) {
        clipboardManager.setPrimaryClip(
            ClipData.newPlainText(
                "Developer Messages", """```
${lines.joinToString("\n") { parseLine(it) }}
```"""))
      }
}
