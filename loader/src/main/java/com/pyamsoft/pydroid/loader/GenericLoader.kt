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
 * The base loader class. Specific implementations are backed by an image loader backend, like
 * Glide, Picasso, or Coil
 */
@Deprecated("Use Coil-Compose in Jetpack Compose UI")
public abstract class GenericLoader<T : Any> protected constructor() : Loader<T> {

  private var startAction: (() -> Unit)? = null
  private var errorAction: (() -> Unit)? = null
  private var completeAction: ((T) -> Unit)? = null
  private var mutator: ((T) -> T)? = null

  /** Mutate a resource and return the mutated copy */
  @CheckResult protected abstract fun mutateImage(resource: T): T

  /** Set an image resource onto an ImageView */
  protected abstract fun setImage(view: ImageView, image: T)

  /** Run a configured loading callback */
  protected fun notifyLoading() {
    startAction?.invoke()
  }

  /** Run a configured error callback */
  protected fun notifyError() {
    errorAction?.invoke()
  }

  /** Run a configured completion callback */
  protected fun notifySuccess(result: T) {
    completeAction?.invoke(result)
  }

  /** Execute a configured mutator, or the identity if none exists */
  @CheckResult
  protected fun executeMutator(data: T): T {
    return mutator?.invoke(data) ?: data
  }

  final override fun onRequest(action: () -> Unit): Loader<T> {
    return this.also { it.startAction = action }
  }

  final override fun onError(action: () -> Unit): Loader<T> {
    return this.also { it.errorAction = action }
  }

  final override fun onLoaded(action: (T) -> Unit): Loader<T> {
    return this.also { it.completeAction = action }
  }

  final override fun mutate(action: (T) -> T): Loader<T> {
    return this.also { it.mutator = action }
  }
}
