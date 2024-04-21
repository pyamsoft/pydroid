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

package com.pyamsoft.pydroid.util

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CheckResult
import androidx.core.app.ActivityOptionsCompat
import com.pyamsoft.pydroid.core.Logger

/** Handles permission requesting */
public interface PermissionRequester {

  /** Request Permissions */
  public fun request(options: ActivityOptionsCompat? = null)
}

/** Register a new permission requester. Must be called before Activity.onStart */
@CheckResult
public fun ComponentActivity.registerPermissionRequester(
    permissions: Array<String>,
    onResponse: (Boolean) -> Unit,
): PermissionRequester {
  val self = this

  var isAlive = true
  if (permissions.isEmpty()) {
    val msg = "Must pass Permissions to permission requester!"
    Logger.w { msg }
    throw IllegalArgumentException(msg)
  }

  val launcher: ActivityResultLauncher<*>
  val requester: PermissionRequester
  if (permissions.size <= 1) {
    val permission = permissions.first()
    launcher =
        self.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
          if (isGranted) {
            Logger.d { "Permissions was granted $permission" }
          } else {
            Logger.w { "Permissions was not granted $permission" }
          }

          onResponse(isGranted)
        }
    requester =
        object : PermissionRequester {

          override fun request(options: ActivityOptionsCompat?) {
            if (!isAlive) {
              Logger.w { "Cannot request permission after Activity.onDestroy" }
              return
            }

            launcher.launch(permission, options)
          }
        }
  } else {
    launcher =
        self.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            results ->
          val ungrantedPermissions = results.filterNot { it.value }.map { it.key }
          val allPermissionsGranted = ungrantedPermissions.isEmpty()

          if (allPermissionsGranted) {
            Logger.d { "All permissions were granted $permissions" }
          } else {
            Logger.w { "Not all permissions were granted $ungrantedPermissions" }
          }

          onResponse(allPermissionsGranted)
        }

    requester =
        object : PermissionRequester {

          override fun request(options: ActivityOptionsCompat?) {
            if (!isAlive) {
              Logger.w { "Cannot request permissions after Activity.onDestroy" }
              return
            }

            launcher.launch(permissions, options)
          }
        }
  }

  self.lifecycle.doOnDestroy {
    isAlive = false
    launcher.unregister()
  }

  return requester
}
