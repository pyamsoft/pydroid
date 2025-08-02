/*
 * Copyright 2025 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.bootstrap.version

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.annotation.CheckResult
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdateLauncher
import com.pyamsoft.pydroid.util.Logger
import com.pyamsoft.pydroid.util.ResultWrapper
import com.pyamsoft.pydroid.util.ifNotCancellation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

internal abstract class AbstractAppUpdateLauncher
protected constructor(
    private val manager: AppUpdateManager,
    private val info: AppUpdateInfo,
    @param:AppUpdateType private val type: Int,
) : AppUpdateLauncher {

  /** App updates can only be used once. */
  private val appUpdateConsumed = MutableStateFlow(false)

  @CheckResult
  private suspend fun startUpdateFlow(activity: Activity): ResultWrapper<AppUpdateResultStatus> =
      suspendCoroutine { cont ->
        val options = AppUpdateOptions.defaultOptions(type)

        Logger.d { "Prompt the user to begin downloading in-app update" }
        manager
            .startUpdateFlow(info, activity, options)
            .addOnCanceledListener {
              Logger.w { "In-app update prompt flow was cancelled" }
              cont.resume(ResultWrapper.success(AppUpdateResultStatus.USER_CANCELLED))
            }
            .addOnFailureListener { err ->
              Logger.e(err) { "In-app update prompt flow encountered an error" }
              cont.resume(ResultWrapper.failure(err))
            }
            .addOnSuccessListener { resultCode ->
              val status =
                  when (resultCode) {
                    Activity.RESULT_OK -> {
                      Logger.d { "In-app update prompt accepted" }
                      AppUpdateResultStatus.ACCEPTED
                    }

                    Activity.RESULT_CANCELED -> {
                      Logger.d { "In-app update prompt cancelled" }
                      AppUpdateResultStatus.USER_CANCELLED
                    }

                    ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                      Logger.d { "In-app update prompt flow returned FAILED code" }
                      AppUpdateResultStatus.IN_APP_UPDATE_FAILED
                    }

                    else -> {
                      Logger.w {
                        "In-app update prompt flow returned unexpected code: $resultCode. Assume FAILED."
                      }
                      AppUpdateResultStatus.IN_APP_UPDATE_FAILED
                    }
                  }
              cont.resume(ResultWrapper.success(status))
            }
      }

  final override fun availableUpdateVersion(): Int {
    return if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
      info.availableVersionCode()
    } else {
      0
    }
  }

  final override fun consumed(): Boolean = appUpdateConsumed.value

  final override suspend fun launchUpdate(
      activity: ComponentActivity
  ): ResultWrapper<AppUpdateResultStatus> =
      withContext(context = Dispatchers.Main) {
        if (appUpdateConsumed.compareAndSet(expect = false, update = true)) {
          try {
            val earlyUpdateOverride = onBeforeUpdateFlowStarted()
            if (earlyUpdateOverride != null) {
              return@withContext earlyUpdateOverride
            }

            return@withContext startUpdateFlow(activity).onSuccess {
              onAfterUpdateFlowStarted(activity, it)
            }
          } catch (e: Throwable) {
            e.ifNotCancellation {
              Logger.e(e) { "Failed to launch in-app update prompt flow." }
              return@withContext ResultWrapper.failure(e)
            }
          }
        } else {
          Logger.w { "In-app update prompt flow can not be consumed twice." }
          return@withContext ResultWrapper.failure(
              AppUpdateLauncher.APP_UPDATE_INFO_ALREADY_CONSUMED_EXCEPTION,
          )
        }
      }

  @CheckResult
  protected open suspend fun onBeforeUpdateFlowStarted(): ResultWrapper<AppUpdateResultStatus>? {
    return null
  }

  protected open suspend fun onAfterUpdateFlowStarted(
      activity: ComponentActivity,
      status: AppUpdateResultStatus
  ) {}
}
