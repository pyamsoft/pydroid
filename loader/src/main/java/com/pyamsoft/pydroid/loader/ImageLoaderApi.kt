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

package com.pyamsoft.pydroid.loader

import android.net.Uri
import androidx.annotation.CheckResult
import androidx.annotation.DrawableRes

/**
 * Generic ImageLoader
 *
 * Loads image resources in the background and loads data into ImageViews or other containers once
 * the image is ready
 */
@Deprecated("Use Landscapist in Jetpack Compose UI")
public interface ImageLoaderApi<I : Any> {

  /** Load a drawable resource */
  @CheckResult public fun load(@DrawableRes resource: Int): Loader<I>

  /** Load a url resource */
  @CheckResult public fun load(uri: Uri): Loader<I>

  /** Load a url resource */
  @CheckResult public fun load(url: String): Loader<I>

  /** Load a byte array resource */
  @CheckResult public fun load(data: ByteArray): Loader<I>
}
