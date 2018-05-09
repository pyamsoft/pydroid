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

package com.pyamsoft.pydroid.ui.util

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.pyamsoft.pydroid.ui.social.Linker
import com.pyamsoft.pydroid.ui.util.Snackbreak.ErrorDetail
import com.pyamsoft.pydroid.util.HyperlinkIntent

private fun snackbreak(
  activity: FragmentActivity,
  view: View
) {
  val detail = ErrorDetail(
      "Unable to navigate",
      "No application is available to handle this URL navigation request"
  )
  Snackbreak.short(activity, view, detail, "Unable to navigate")
}

internal fun HyperlinkIntent.navigate(
  activity: FragmentActivity,
  view: View
) {
  navigate { snackbreak(activity, view) }
}

internal fun Linker.clickAppPage(
  activity: FragmentActivity,
  view: View
) {
  clickAppPage { snackbreak(activity, view) }
}

internal fun Linker.clickBlogger(
  activity: FragmentActivity,
  view: View
) {
  clickBlogger { snackbreak(activity, view) }
}

internal fun Linker.clickGooglePlay(
  activity: FragmentActivity,
  view: View
) {
  clickGooglePlay { snackbreak(activity, view) }
}

internal fun Linker.clickGooglePlus(
  activity: FragmentActivity,
  view: View
) {
  clickGooglePlus { snackbreak(activity, view) }
}

internal fun Linker.clickFacebook(
  activity: FragmentActivity,
  view: View
) {
  clickFacebook { snackbreak(activity, view) }
}
