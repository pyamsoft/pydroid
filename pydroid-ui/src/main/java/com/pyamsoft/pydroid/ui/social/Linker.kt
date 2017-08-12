/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.social

import android.content.Context
import com.pyamsoft.pydroid.util.NetworkUtil

object Linker {

  private const val BASE_MARKET = "market://details?id="
  private const val FACEBOOK = "https://www.facebook.com/pyamsoftware"
  private const val GOOGLE_PLAY_DEVELOPER_PAGE = "https://play.google.com/store/apps/dev?id=5257476342110165153"
  private const val GOOGLE_PLUS = "https://plus.google.com/+Pyamsoft-officialBlogspot/posts"
  private const val OFFICIAL_BLOG = "https://pyamsoft.blogspot.com/"

  @JvmStatic fun clickAppPage(context: Context, link: String) {
    NetworkUtil.newLink(context.applicationContext, BASE_MARKET + link)
  }

  @JvmStatic fun clickGooglePlay(context: Context) {
    NetworkUtil.newLink(context.applicationContext, GOOGLE_PLAY_DEVELOPER_PAGE)
  }

  @JvmStatic fun clickGooglePlus(context: Context) {
    NetworkUtil.newLink(context.applicationContext, GOOGLE_PLUS)
  }

  @JvmStatic fun clickBlogger(context: Context) {
    NetworkUtil.newLink(context.applicationContext, OFFICIAL_BLOG)
  }

  @JvmStatic fun clickFacebook(context: Context) {
    NetworkUtil.newLink(context.applicationContext, FACEBOOK)
  }

}
