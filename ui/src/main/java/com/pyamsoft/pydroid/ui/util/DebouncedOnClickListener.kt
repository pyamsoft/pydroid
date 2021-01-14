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

package com.pyamsoft.pydroid.ui.util

import android.view.View
import androidx.annotation.CheckResult

/**
 * Click listener which debounces all other click events for the frame
 */
public abstract class DebouncedOnClickListener protected constructor() : View.OnClickListener {

    /**
     * On click
     */
    final override fun onClick(view: View) {
        if (enabled) {
            enabled = false
            view.post(enableAgain)
            doClick(view)
        }
    }

    /**
     * On click
     */
    protected abstract fun doClick(view: View)

    public companion object {

        private var enabled = true
        private val enableAgain = Runnable { enabled = true }

        /**
         * Create a new debouncing click listener
         */
        @CheckResult
        @JvmStatic
        public inline fun create(crossinline func: (View) -> Unit): View.OnClickListener {
            return object : DebouncedOnClickListener() {
                override fun doClick(view: View) {
                    func(view)
                }
            }
        }
    }
}

/**
 * Convert a click listener into a debouncing one.
 */
@CheckResult
public fun View.OnClickListener.debounce(): View.OnClickListener {
    return DebouncedOnClickListener.create { this.onClick(it) }
}
