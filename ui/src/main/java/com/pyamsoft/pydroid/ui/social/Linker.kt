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

import android.content.Context
import android.view.View
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.ui.util.navigate
import com.pyamsoft.pydroid.util.hyperlink

class Linker private constructor(
  private val context: Context,
  private val appLink: String
) {

  fun clickAppPage(view: View) {
    "$BASE_MARKET$appLink".hyperlink(context)
        .navigate(view)
  }

  fun clickGooglePlay(view: View) {
    GOOGLE_PLAY_DEVELOPER_PAGE.hyperlink(context)
        .navigate(view)
  }

  fun clickGooglePlus(view: View) {
    GOOGLE_PLUS.hyperlink(context)
        .navigate(view)
  }

  fun clickBlogger(view: View) {
    OFFICIAL_BLOG.hyperlink(context)
        .navigate(view)
  }

  fun clickFacebook(view: View) {
    FACEBOOK.hyperlink(context)
        .navigate(view)
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
      appLink: String
    ): Linker {
      return Linker(context.applicationContext, appLink)
    }
  }
}
