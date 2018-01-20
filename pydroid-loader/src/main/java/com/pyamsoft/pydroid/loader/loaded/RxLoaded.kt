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

package com.pyamsoft.pydroid.loader.loaded

import android.arch.lifecycle.Lifecycle.Event.ON_DESTROY
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.Disposable

class RxLoaded(private var disposable: Disposable) : Loaded, LifecycleObserver {

    private var lifeCycleOwner: LifecycleOwner? = null

    override fun bind(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(this)
        lifeCycleOwner = owner
    }

    @OnLifecycleEvent(ON_DESTROY)
    internal fun unbindOnDestroy() {
        lifeCycleOwner?.lifecycle?.removeObserver(this)
        lifeCycleOwner = null

        if (!disposable.isDisposed) {
            disposable = disposable.clear()
        }
    }
}
