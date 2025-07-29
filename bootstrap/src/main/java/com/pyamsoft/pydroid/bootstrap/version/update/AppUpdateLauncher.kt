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

package com.pyamsoft.pydroid.bootstrap.version.update

import androidx.activity.ComponentActivity
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.version.AppUpdateResultStatus
import com.pyamsoft.pydroid.util.ResultWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.annotations.TestOnly

/** In app update launcher */
public interface AppUpdateLauncher {

  /**
   * Has this launcher already been used?
   *
   * AppUpdateInfo objects can only be used once.
   * https://developer.android.com/reference/com/google/android/play/core/appupdate/AppUpdateManager.html#startUpdateFlowForResult(com.google.android.play.core.appupdate.AppUpdateInfo,%20android.app.Activity,%20com.google.android.play.core.appupdate.AppUpdateOptions,%20int)
   */
  @CheckResult public fun consumed(): Boolean

  /** Get the available update version */
  @CheckResult public fun availableUpdateVersion(): Int

  /**
   * Begin an update
   *
   * https://developer.android.com/reference/com/google/android/play/core/appupdate/AppUpdateManager.html#startUpdateFlowForResult(com.google.android.play.core.appupdate.AppUpdateInfo,%20android.app.Activity,%20com.google.android.play.core.appupdate.AppUpdateOptions,%20int)
   */
  @CheckResult
  public suspend fun launchUpdate(activity: ComponentActivity): ResultWrapper<AppUpdateResultStatus>

  private abstract class AbstractEmptyAppUpdateLauncher(
      private val status: AppUpdateResultStatus,
  ) : AppUpdateLauncher {

    /** App updates can only be used once. */
    private val appUpdateConsumed = MutableStateFlow(false)

    override fun consumed(): Boolean = appUpdateConsumed.value

    final override suspend fun launchUpdate(
        activity: ComponentActivity
    ): ResultWrapper<AppUpdateResultStatus> =
        if (appUpdateConsumed.compareAndSet(expect = false, update = true)) {
          ResultWrapper.success(status)
        } else {
          ResultWrapper.failure(APP_UPDATE_INFO_ALREADY_CONSUMED_EXCEPTION)
        }
  }

  public companion object {

    /** App updates can only be used once. */
    internal val APP_UPDATE_INFO_ALREADY_CONSUMED_EXCEPTION: Exception =
        IllegalStateException("Updates can only be checked a single time.")

    /** Create a no-op update launcher */
    @JvmStatic
    @CheckResult
    public fun empty(
        status: AppUpdateResultStatus = AppUpdateResultStatus.ACCEPTED,
    ): AppUpdateLauncher {
      return object : AbstractEmptyAppUpdateLauncher(status) {

        override fun availableUpdateVersion(): Int = 0
      }
    }

    /** Create a test update launcher */
    @TestOnly
    @JvmStatic
    @CheckResult
    public fun test(versionCode: Int): AppUpdateLauncher {
      return object :
          AbstractEmptyAppUpdateLauncher(
              status = AppUpdateResultStatus.ACCEPTED,
          ) {

        override fun availableUpdateVersion(): Int = versionCode
      }
    }
  }
}
