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

import android.support.annotation.ColorRes

abstract class GenericLoader<T : Any> protected constructor() : Loader<T> {

  protected var startAction: (() -> Unit)? = null
  protected var errorAction: ((Throwable) -> Unit)? = null
  protected var completeAction: ((T) -> Unit)? = null
  protected var tint: Int = 0

  final override fun withStartAction(startAction: () -> Unit): Loader<T> {
    this.startAction = startAction
    return this
  }

  final override fun withCompleteAction(completeAction: (T) -> Unit): Loader<T> {
    this.completeAction = completeAction
    return this
  }

  final override fun withErrorAction(errorAction: (Throwable) -> Unit): Loader<T> {
    this.errorAction = errorAction
    return this
  }

  final override fun tint(@ColorRes color: Int): Loader<T> {
    this.tint = color
    return this
  }
}
