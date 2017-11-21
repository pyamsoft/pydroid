/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

@file:JvmName("DisposableHelper")

package com.pyamsoft.pydroid.helper

import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables

/**
 * Extension function for Kotlin
 *
 * If the Disposable is non-null, disposes of it and then returns the new disposable
 *
 * The new disposable is by default, the empty disposable, so that all memory references
 * held by the disposable are marked for GC
 */
@JvmOverloads
fun Disposable?.clear(disposable: Disposable = Disposables.empty()): Disposable {
    if (this == null) {
        return disposable
    }

    if (!isDisposed) {
        dispose()
    }

    return disposable
}
