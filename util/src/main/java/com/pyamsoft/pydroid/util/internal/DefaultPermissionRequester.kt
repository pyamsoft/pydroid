/*
 * Copyright 2024 pyamsoft
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

package com.pyamsoft.pydroid.util.internal

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.util.PermissionRequester

internal class DefaultPermissionRequester
internal constructor(
    private val permissions: Array<String>,
) : PermissionRequester {

  @CheckResult
  private fun createRequester(
      launcher: ActivityResultLauncher<Array<String>>,
      onResponse: (Boolean) -> Unit
  ): PermissionRequester.Requester {
    return object : PermissionRequester.Requester {

      private var theLauncher: ActivityResultLauncher<Array<String>>? = launcher
      private var responseCallback: ((Boolean) -> Unit)? = onResponse

      override fun requestPermissions() {
        // If this is already unregistered, this does nothing
        if (permissions.isEmpty()) {
          Logger.d { "No permissions requested, API level differences? Fallback => true" }
          responseCallback?.invoke(true)
        } else {
          theLauncher?.launch(permissions)
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

  private inline fun handlePermissionResults(
      onResponse: (Boolean) -> Unit,
      results: Map<String, Boolean>
  ) {
    val ungrantedPermissions = results.filterNot { it.value }.map { it.key }
    val allPermissionsGranted = ungrantedPermissions.isEmpty()

    if (allPermissionsGranted) {
      Logger.d { "All permissions were granted $permissions" }
    } else {
      Logger.w { "Not all permissions were granted $ungrantedPermissions" }
    }

    onResponse(allPermissionsGranted)
  }

  override fun registerRequester(
      activity: ComponentActivity,
      onResponse: (Boolean) -> Unit
  ): PermissionRequester.Requester {
    val launcher =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
          handlePermissionResults(onResponse, it)
        }

    return createRequester(launcher, onResponse)
  }
}
