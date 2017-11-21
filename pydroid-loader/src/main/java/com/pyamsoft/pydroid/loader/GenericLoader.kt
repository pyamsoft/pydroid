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
