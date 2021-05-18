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

import android.widget.ImageView
import androidx.annotation.CheckResult

/**
 * The Loader interface which deals with how to format image loading requests into a specific
 * backend
 */
public interface Loader<T : Any> {

  /**
   * Called when the request begins running
   *
   * Not guaranteed in immediate() mode
   */
  @CheckResult public fun onRequest(action: () -> Unit): Loader<T>

  /**
   * Called when an error occurs during loading
   *
   * Not guaranteed in immediate() mode
   */
  @CheckResult public fun onError(action: () -> Unit): Loader<T>

  /**
   * Called when the resource is loaded into the target
   *
   * Not guaranteed in immediate() mode
   */
  @CheckResult public fun onLoaded(action: (T) -> Unit): Loader<T>

  /**
   * Mutate the loaded resource
   *
   * Not guaranteed in immediate() mode
   */
  @CheckResult public fun mutate(action: (T) -> T): Loader<T>

  /** Load the resource into an ImageView */
  @CheckResult public fun into(imageView: ImageView): Loaded

  /** Load the resource into a target */
  @CheckResult public fun into(target: ImageTarget<T>): Loaded

  /**
   * Perform all loading work in a blocking manner and return the resource immediately
   *
   * Not all ImageLoader parameters support immediate loading and will return null
   */
  @CheckResult
  @Deprecated("You almost always want to use something else")
  public fun immediate(): T?
}
