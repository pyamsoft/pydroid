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

package com.pyamsoft.pydroid.ui.util

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.util.doOnStart

@JvmOverloads
inline fun FragmentManager.commit(
    owner: LifecycleOwner,
    immediate: Boolean = false,
    crossinline transaction: FragmentTransaction.() -> FragmentTransaction
) {
    return this.commit(owner.lifecycle, immediate, transaction)
}

@JvmOverloads
inline fun FragmentManager.commit(
    lifecycle: Lifecycle,
    immediate: Boolean = false,
    crossinline transaction: FragmentTransaction.() -> FragmentTransaction
) {
    lifecycle.doOnStart {
        this.beginTransaction()
            .run(transaction)
            .commit()

        if (immediate) {
            this.executePendingTransactions()
        }
    }
}

inline fun FragmentManager.commitNow(
    owner: LifecycleOwner,
    crossinline transaction: FragmentTransaction.() -> FragmentTransaction
) {
    return this.commitNow(owner.lifecycle, transaction)
}

inline fun FragmentManager.commitNow(
    lifecycle: Lifecycle,
    crossinline transaction: FragmentTransaction.() -> FragmentTransaction
) {
    lifecycle.doOnStart {
        this.beginTransaction()
            .run(transaction)
            .commitNow()
    }
}
