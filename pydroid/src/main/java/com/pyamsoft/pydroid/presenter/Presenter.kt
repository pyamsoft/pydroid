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

package com.pyamsoft.pydroid.presenter

import android.arch.lifecycle.Lifecycle.Event.ON_CREATE
import android.arch.lifecycle.Lifecycle.Event.ON_DESTROY
import android.arch.lifecycle.Lifecycle.Event.ON_PAUSE
import android.arch.lifecycle.Lifecycle.Event.ON_RESUME
import android.arch.lifecycle.Lifecycle.Event.ON_START
import android.arch.lifecycle.Lifecycle.Event.ON_STOP
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class Presenter<V : Any> protected constructor() : LifecycleObserver {

    private val disposables: CompositeDisposable = CompositeDisposable()
    protected var view: V? = null
        private set
    private var lifecycleOwner: LifecycleOwner? = null

    fun bind(owner: LifecycleOwner, view: V) {
        this.view = view
        this.lifecycleOwner = owner
        owner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(ON_CREATE)
    private fun performCreate() {
        onCreate()
    }

    protected open fun onCreate() {
    }

    @OnLifecycleEvent(ON_START)
    private fun performStart() {
        onStart()
    }

    protected open fun onStart() {
    }

    @OnLifecycleEvent(ON_RESUME)
    private fun performResume() {
        onResume()
    }

    protected open fun onResume() {
    }

    @OnLifecycleEvent(ON_PAUSE)
    private fun performPause() {
        onPause()
    }

    protected open fun onPause() {
    }

    @OnLifecycleEvent(ON_STOP)
    private fun performStop() {
        onStop()
    }

    protected open fun onStop() {
    }

    @OnLifecycleEvent(ON_DESTROY)
    private fun performDestroy() {
        // Unbind the view
        this.view = null
        onDestroy()

        // Clear disposables after onDestroy incase something accidentally subscribes
        disposables.clear()

        // Remove the lifecycle observer since we are dead
        lifecycleOwner?.lifecycle?.removeObserver(this)
        lifecycleOwner = null
    }

    protected open fun onDestroy() {
    }

    /**
     * Add a disposable to the internal list, dispose it onUnbind
     */
    protected inline fun dispose(func: () -> Disposable) {
        dispose(func())
    }

    /**
     * Add a disposable to the internal list, dispose it onUnbind
     */
    protected fun dispose(disposable: Disposable) {
        disposables.add(disposable)
    }
}
