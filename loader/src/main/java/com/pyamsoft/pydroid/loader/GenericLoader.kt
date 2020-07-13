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
 *
 */

package com.pyamsoft.pydroid.loader

import android.widget.ImageView
import androidx.annotation.CheckResult

abstract class GenericLoader<T : Any> protected constructor() : Loader<T> {

    protected var startAction: (() -> Unit)? = null
    protected var errorAction: (() -> Unit)? = null
    protected var completeAction: ((T) -> Unit)? = null
    protected var mutator: ((T) -> T)? = null

    @CheckResult
    protected abstract fun mutateImage(resource: T): T

    protected abstract fun setImage(view: ImageView, image: T)

    @CheckResult
    protected abstract fun immediateResource(): T?

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

    final override fun immediate(): T? {
        startAction?.invoke()
        try {
            val resource: T? = immediateResource()
            if (resource == null) {
                errorAction?.invoke()
            } else {
                val mutated = mutator?.invoke(mutateImage(resource)) ?: resource
                completeAction?.invoke(mutated)
                return mutated
            }
        } catch (e: Exception) {
            errorAction?.invoke()
        }

        return null
    }
}
