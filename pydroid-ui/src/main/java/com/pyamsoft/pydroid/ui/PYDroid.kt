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

package com.pyamsoft.pydroid.ui

import android.os.StrictMode
import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.PYDroidModule
import com.pyamsoft.pydroid.loader.LoaderModule
import com.pyamsoft.pydroid.ui.about.UiLicenses
import timber.log.Timber

/**
 * PYDroid library entry point
 *
 * This actually does not inherit from the SimpleInjector interface because we want the
 * obtain method to stay internal
 */
object PYDroid {

    private var component: PYDroidComponent? = null

    @JvmStatic
    private fun setStrictMode() {
        StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().penaltyDeath().permitDiskReads()
                        .permitDiskWrites().penaltyFlashScreen().build())
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build())
    }

    /**
     * Access point for library component graph
     */
    @JvmStatic
    @CheckResult
    internal fun obtain(): PYDroidComponent {
        val obj = component
        if (obj == null) {
            throw IllegalStateException(
                    "PYDroid is not initialized. Please call PYDroid.init() before attempting to obtain.")
        } else {
            return obj
        }
    }

    /**
     * Create the library entry point
     */
    @JvmStatic
    private fun initialize(pydroidModule: PYDroidModule, loaderModule: LoaderModule) {
        component = PYDroidComponentImpl(pydroidModule, loaderModule)
        if (pydroidModule.isDebug) {
            Timber.plant(Timber.DebugTree())
            setStrictMode()
        }
        UiLicenses.addLicenses()
    }

    /**
     * Initialize the library
     *
     * You should carry the passed modules with you to any other component graphs or you will have "doubled" singletons
     */
    @JvmStatic
    fun init(pydroidModule: PYDroidModule, loaderModule: LoaderModule) {
        if (component == null) {
            initialize(pydroidModule, loaderModule)
        }
    }
}
