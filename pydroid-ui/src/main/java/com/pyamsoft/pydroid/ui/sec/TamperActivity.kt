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

package com.pyamsoft.pydroid.ui.sec

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.rating.RatingActivity
import com.pyamsoft.pydroid.ui.util.DialogUtil
import timber.log.Timber

abstract class TamperActivity : RatingActivity() {

    internal var debugMode: Boolean = false

    @CallSuper override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PYDroid.obtain().inject(this)
    }

    /**
     * Returns true if the application has been tampered with, false if not
     */
    @CheckResult private fun applicationIsTampered(): Boolean {
        // Check if we are renamed
        if (applicationContext.packageName.compareTo(safePackageName) != 0) {
            Timber.e("Application is potentially re-named")
            return true
        }

        // Check that we were installed from the play store.
        val installer = applicationContext.packageManager.getInstallerPackageName(safePackageName)
        if (debugMode) {
            if (installer == null) {
                Timber.i("Application is installed from APK. This is fine in DEBUG mode")
                return false
            } else {
                Timber.e("DEBUG Application is not installed from APK")
                return true
            }
        } else {
            if (installer == null) {
                Timber.e("RELEASE Application is not installed from Google Play Store")
                Timber.e("Installer: NULL")
                return true
            }

            if (GOOGLE_PLAY_STORE_INSTALLER.compareTo(installer) != 0) {
                Timber.e("RELEASE Application is not installed from Google Play Store")
                Timber.e("Installer: %s", installer)
                return true
            }

            Timber.d("Application is safe")
            return false
        }
    }

    @CallSuper
    override fun onPostResume() {
        super.onPostResume()
        if (applicationIsTampered()) {
            Timber.e("Application has been tampered with, notify user")
            DialogUtil.guaranteeSingleDialogFragment(this, TamperDialog(), "tamper")
        }
    }

    /**
     * The package name that we expect to be running under
     */
    @get:CheckResult protected abstract val safePackageName: String

    companion object {

        private const val GOOGLE_PLAY_STORE_INSTALLER = "com.android.vending"
    }
}
