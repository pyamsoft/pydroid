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

package com.pyamsoft.pydroid.loader

import android.support.annotation.CheckResult
import android.support.annotation.ColorRes
import android.widget.ImageView
import com.pyamsoft.pydroid.loader.loaded.Loaded
import com.pyamsoft.pydroid.loader.targets.Target

abstract class GenericLoader<out L, T> protected constructor() {

  protected var startAction: (Target<T>) -> Unit = {}
  protected var errorAction: (Target<T>) -> Unit = {}
  protected var completeAction: (Target<T>) -> Unit = {}
  protected var tint: Int = 0

  @CheckResult abstract fun tint(@ColorRes color: Int): L

  @CheckResult abstract fun withStartAction(startAction: (Target<T>) -> Unit): L

  @CheckResult abstract fun withErrorAction(errorAction: (Target<T>) -> Unit): L

  @CheckResult abstract fun withCompleteAction(completeAction: (Target<T>) -> Unit): L

  @CheckResult abstract fun into(imageView: ImageView): Loaded

  @CheckResult abstract fun into(target: Target<T>): Loaded
}
