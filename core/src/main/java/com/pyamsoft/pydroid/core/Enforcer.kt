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

package com.pyamsoft.pydroid.core

import android.os.Looper
import androidx.annotation.CheckResult
import androidx.annotation.VisibleForTesting


/**
 * Interface for the enforcer
 */
public interface Associate {

    /**
     * Throws an exception if the current thread is the Main or UI thread
     */
    public fun assertOffMainThread()

    /**
     * Throws an exception if the current thread is not the Main or UI thread
     */
    public fun assertOnMainThread()

}

/**
 * Reggie is the normal enforcer, he expects correctly threaded contexts of execution
 */
internal class Reggie : Associate {

    private val mainLooper by lazy { requireNotNull(Looper.getMainLooper()) }

    @CheckResult
    private fun isMainThread(): Boolean {
        return mainLooper.thread == Thread.currentThread()
    }

    /**
     * Throws an exception if the current thread is the Main or UI thread
     */
    override fun assertOffMainThread() {
        if (isMainThread()) {
            throw AssertionError("This operation must be OFF the Main/UI thread!")
        }
    }

    /**
     * Throws an exception if the current thread is not the Main or UI thread
     */
    override fun assertOnMainThread() {
        if (!isMainThread()) {
            throw AssertionError("This operation must be ON the Main/UI thread!")
        }
    }

}

/**
 * Enforce expected threading contexts
 */
public object Enforcer : Associate {

    private var associate: Associate = Reggie()

    override fun assertOffMainThread() {
        return associate.assertOffMainThread()
    }

    override fun assertOnMainThread() {
        return associate.assertOnMainThread()
    }

    /**
     * Assign a different associate for tests
     */
    @VisibleForTesting
    internal fun setAssociate(associate: Associate) {
        this.associate = associate
    }
}
