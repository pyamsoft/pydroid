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
import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.helper.ThreadSafe.DynamicSingleton
import com.pyamsoft.pydroid.helper.ThreadSafe.Singleton
import com.pyamsoft.pydroid.presenter.Presenter
import com.pyamsoft.pydroid.util.NetworkUtil

class Linker private constructor(context: Context) : Presenter() {

  private val appContext: Context = context.applicationContext

  fun clickAppPage(link: String) {
    NetworkUtil.newLink(appContext, BASE_MARKET + link)
  }

  fun clickGooglePlay() {
    NetworkUtil.newLink(appContext, GOOGLE_PLAY_DEVELOPER_PAGE)
  }

  fun clickGooglePlus() {
    NetworkUtil.newLink(appContext, GOOGLE_PLUS)
  }

  fun clickBlogger() {
    NetworkUtil.newLink(appContext, OFFICIAL_BLOG)
  }

  fun clickFacebook() {
    NetworkUtil.newLink(appContext, FACEBOOK)
  }

  companion object {

    private const val BASE_MARKET = "market://details?id="
    private const val FACEBOOK = "https://www.facebook.com/pyamsoftware"
    private const val GOOGLE_PLAY_DEVELOPER_PAGE = "https://play.google.com/store/apps/dev?id=5257476342110165153"
    private const val GOOGLE_PLUS = "https://plus.google.com/+Pyamsoft-officialBlogspot/posts"
    private const val OFFICIAL_BLOG = "https://pyamsoft.blogspot.com/"

    private val singleton = DynamicSingleton<Linker>(null)

    @CheckResult fun with(context: Context): Linker {
      return singleton.access {
        Linker(context.applicationContext)
      }
    }
  }
}
