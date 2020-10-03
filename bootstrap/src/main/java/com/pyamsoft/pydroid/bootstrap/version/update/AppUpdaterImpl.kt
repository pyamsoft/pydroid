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

package com.pyamsoft.pydroid.bootstrap.version.update

import android.content.Context
import androidx.annotation.CheckResult
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.requestCompleteUpdate
import com.pyamsoft.pydroid.core.Enforcer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.resume

internal class AppUpdaterImpl internal constructor(
    private val context: Context,
    private val debug: Boolean
) : AppUpdater {

    private val manager by lazy {
        AppUpdateManagerFactory.create(context.applicationContext)
    }

    @CheckResult
    private inline fun createStatusListener(crossinline onDownloadComplete: () -> Unit): InstallStateUpdatedListener {
        return InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                onDownloadComplete()
            }
        }
    }

    override suspend fun complete() = withContext(context = Dispatchers.Main) {
        Enforcer.assertOnMainThread()

        Timber.d("COMPLETED UPDATE")
        manager.requestCompleteUpdate()
    }

    override suspend fun watchForDownloadComplete(onDownloadComplete: () -> Unit) =
        withContext(context = Dispatchers.IO) {
            Enforcer.assertOffMainThread()

            return@withContext suspendCancellableCoroutine<Unit> { continuation ->
                if (debug) {
                    Timber.d("Cannot listen for in-app updates in DEBUG mode")
                    continuation.cancel()
                    return@suspendCancellableCoroutine
                }

                val listener = createStatusListener(onDownloadComplete)

                Timber.d("Listen for install status DOWNLOADED")
                manager.registerListener(listener)

                continuation.invokeOnCancellation {
                    Timber.d("Stop listening for install status")
                    manager.unregisterListener(listener)
                }
            }
        }

    override suspend fun checkForUpdate(): AppUpdateLauncher =
        withContext(context = Dispatchers.IO) {
            Enforcer.assertOffMainThread()

            return@withContext suspendCancellableCoroutine { continuation ->
                if (debug) {
                    Timber.d("Cannot check for in-app updates in DEBUG mode")
                    continuation.cancel()
                    return@suspendCancellableCoroutine
                }

                manager.appUpdateInfo
                    .addOnFailureListener { error ->
                        Timber.e(error, "Failed to resolve app update info task")
                        continuation.resume(AppUpdateLauncher.empty())
                    }
                    .addOnSuccessListener { info ->
                        Timber.d("App Update info received: $info")
                        if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                            if (info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                                Timber.d("Update is available and flexible")
                                continuation.resume(
                                    PlayStoreAppUpdateLauncher(
                                        manager,
                                        info,
                                        AppUpdateType.FLEXIBLE
                                    )
                                )
                                return@addOnSuccessListener
                            }
                        }

                        Timber.d("Update is not available")
                        continuation.resume(AppUpdateLauncher.empty())
                    }
            }
        }
}
