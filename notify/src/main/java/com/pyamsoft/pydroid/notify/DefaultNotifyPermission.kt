/*
 * Copyright 2022 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.notify

import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CheckResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.pyamsoft.pydroid.core.Logger

internal class DefaultNotifyPermission internal constructor() : NotifyPermission {

  @CheckResult
  private fun createRequester(
      launcher: ActivityResultLauncher<String>,
      onResponse: (Boolean) -> Unit
  ): NotifyPermission.Requester {
    return object : NotifyPermission.Requester {

      private var theLauncher: ActivityResultLauncher<String>? = launcher
      private var responseCallback: ((Boolean) -> Unit)? = onResponse

      override fun requestPermissions() {
        // If this is already unregistered, this does nothing
        if (Build.VERSION.SDK_INT >= 33) {
          theLauncher?.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
          Logger.d("No need to request Notification permissions for API < 33, just fire callback")
          responseCallback?.invoke(true)
        }
      }

      override fun unregister() {
        theLauncher?.unregister()

        // Clear memory to avoid leaks
        theLauncher = null
        responseCallback = null
      }
    }
  }

  override fun registerRequester(
      activity: FragmentActivity,
      onResponse: (Boolean) -> Unit
  ): NotifyPermission.Requester {
    val launcher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
          onResponse(granted)
        }

    return createRequester(launcher, onResponse)
  }

  override fun registerRequester(
      fragment: Fragment,
      onResponse: (Boolean) -> Unit
  ): NotifyPermission.Requester {
    val launcher =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
          onResponse(granted)
        }

    return createRequester(launcher, onResponse)
  }
}
