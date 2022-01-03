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

package com.pyamsoft.pydroid.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.core.ResultWrapper

/** Turn a string into a hyperlink intent */
@CheckResult
@Deprecated("Migrate to Jetpack Compose")
public fun String.hyperlink(c: Context): HyperlinkIntent {
  val intent = Intent(Intent.ACTION_VIEW).also { it.data = Uri.parse(this) }

  return HyperlinkIntent(c.applicationContext, intent)
}

/** Intent class that knows how to navigate to a given hyperlink */
public data class HyperlinkIntent
internal constructor(
    /** PublishedApi internal for navigate() inline */
    @PublishedApi internal val context: Context,

    /** PublishedApi internal for navigate() inline */
    @PublishedApi internal val intent: Intent
) {

  /**
   * Navigate to the hyperlink.
   *
   * On success, this will return Unit and perform navigation On failure, this will return the
   * ActivityNotFound exception.
   */
  @CheckResult
  public fun navigate(): ResultWrapper<Unit> {
    val appContext = context.applicationContext
    return try {
      val result = appContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
      ResultWrapper.success(result)
    } catch (e: ActivityNotFoundException) {
      ResultWrapper.failure(e)
    }
  }
}
