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
import com.pyamsoft.pydroid.ui.social.Linker
import com.pyamsoft.pydroid.util.HyperlinkIntent

private fun snackbreak(
  view: View
) {
  Snackbreak.short(view, "No application can handle this URL")
      .show()
}

fun HyperlinkIntent.navigate(view: View) {
  navigate { snackbreak(view) }
}

internal fun Linker.clickAppPage(view: View) {
  clickAppPage { snackbreak(view) }
}

internal fun Linker.clickBlogger(view: View) {
  clickBlogger { snackbreak(view) }
}

internal fun Linker.clickGooglePlay(view: View) {
  clickGooglePlay { snackbreak(view) }
}

internal fun Linker.clickGooglePlus(view: View) {
  clickGooglePlus { snackbreak(view) }
}

internal fun Linker.clickFacebook(view: View) {
  clickFacebook { snackbreak(view) }
}
