/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.ui.social

import android.content.ActivityNotFoundException
import android.content.Context
import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.util.hyperlink

class Linker private constructor(
  private val context: Context,
  private val appLink: String,
  private val onNavigationErrorHandler: (ActivityNotFoundException) -> Unit
) {

  fun clickAppPage() {
    "$BASE_MARKET$appLink".hyperlink(context)
        .navigate(onNavigationErrorHandler)
  }

  fun clickGooglePlay() {
    GOOGLE_PLAY_DEVELOPER_PAGE.hyperlink(context)
        .navigate(onNavigationErrorHandler)
  }

  fun clickGooglePlus() {
    GOOGLE_PLUS.hyperlink(context)
        .navigate(onNavigationErrorHandler)
  }

  fun clickBlogger() {
    OFFICIAL_BLOG.hyperlink(context)
        .navigate(onNavigationErrorHandler)
  }

  fun clickFacebook() {
    FACEBOOK.hyperlink(context)
        .navigate(onNavigationErrorHandler)
  }

  companion object {

    private const val BASE_MARKET = "market://details?id="
    private const val FACEBOOK = "https://www.facebook.com/pyamsoftware"
    private const val GOOGLE_PLAY_DEVELOPER_PAGE =
      "https://play.google.com/store/apps/dev?id=5257476342110165153"
    private const val GOOGLE_PLUS = "https://plus.google.com/+Pyamsoft-officialBlogspot/posts"
    private const val OFFICIAL_BLOG = "https://pyamsoft.blogspot.com/"

    @JvmStatic
    @CheckResult
    fun create(
      context: Context,
      appLink: String,
      onNavigationErrorHandler: (ActivityNotFoundException) -> Unit
    ): Linker {
      return Linker(context.applicationContext, appLink, onNavigationErrorHandler)
    }
  }
}
