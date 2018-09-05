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

package com.pyamsoft.pydroid.loader

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.CheckResult

interface Loader<T : Any> {

  @CheckResult
  fun onRequest(action: () -> Unit): Loader<T>

  @CheckResult
  fun onError(action: (Drawable?) -> Unit): Loader<T>

  @CheckResult
  fun onLoaded(action: (T) -> Unit): Loader<T>

  @CheckResult
  fun mutate(action: (T) -> T): Loader<T>

  @CheckResult
  fun into(imageView: ImageView): Loaded
}
