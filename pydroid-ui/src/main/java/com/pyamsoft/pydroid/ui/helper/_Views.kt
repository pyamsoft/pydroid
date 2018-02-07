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

package com.pyamsoft.pydroid.ui.helper

import android.view.View

inline fun <T : View> T.postWith(crossinline func: (T) -> Unit) {
  post { func(this) }
}

inline fun <T : View> T.postWith(
  delay: Long,
  crossinline func: (T) -> Unit
) {
  if (delay == 0L) {
    postWith(func)
  } else {
    postDelayed({ func(this) }, delay)
  }
}

inline fun <T : View> T.postOnAnimationWith(crossinline func: (T) -> Unit) {
  postOnAnimation { func(this) }
}

inline fun <T : View> T.postOnAnimationWith(
  delay: Long,
  crossinline func: (T) -> Unit
) {
  if (delay == 0L) {
    postOnAnimationWith(func)
  } else {
    postOnAnimationDelayed({ func(this) }, delay)
  }
}
