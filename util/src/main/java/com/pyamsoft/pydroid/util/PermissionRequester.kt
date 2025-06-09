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

/** Simplify the code for requesting permissions via the ActivityResultContract */
public class PermissionRequester private constructor() {

  public interface Launcher {

    public fun launch(permission: String)

    public fun launch(permissions: Array<String>)

    public fun launch(permissions: Iterable<String>)

    public fun launch(
        permissions: Array<String>,
        options: ActivityOptionsCompat?,
    )

    public fun launch(
        permissions: Iterable<String>,
        options: ActivityOptionsCompat?,
    )
  }

  @CheckResult
  private fun createMultiplePermissionRequester(
      activity: ComponentActivity,
      onResponse: (Boolean) -> Unit,
  ): ActivityResultLauncher<Array<String>> {
    return activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { results ->
          val ungrantedPermissions = results.filterNot { it.value }.map { it.key }
          val allPermissionsGranted = ungrantedPermissions.isEmpty()
          if (allPermissionsGranted) {
            Logger.d { "All permissions granted" }
          } else {
            Logger.w { "Did not grant all permissions: $ungrantedPermissions" }
          }

          onResponse(allPermissionsGranted)
        }
  }

  /** Register to the activity */
  @CheckResult
  public fun register(
      activity: ComponentActivity,
      onResponse: (Boolean) -> Unit,
  ): Launcher {
    val requester = createMultiplePermissionRequester(activity, onResponse)

    activity.doOnDestroy { requester.unregister() }

    return object : Launcher {
      override fun launch(permission: String) {
        launch(arrayOf(permission))
      }

      override fun launch(permissions: Iterable<String>) {
        launch(permissions.toList().toTypedArray())
      }

      override fun launch(permissions: Array<String>) {
        launch(permissions, null)
      }

      override fun launch(permissions: Iterable<String>, options: ActivityOptionsCompat?) {
        launch(permissions.toList().toTypedArray(), options)
      }

      override fun launch(permissions: Array<String>, options: ActivityOptionsCompat?) {
        requester.launch(permissions, options)
      }
    }
  }

  public companion object {

    /** Simple shortcut method */
    @JvmStatic
    @CheckResult
    public fun createAndRegister(
        activity: ComponentActivity,
        onResponse: (Boolean) -> Unit
    ): Launcher {
      return PermissionRequester()
          .register(
              activity = activity,
              onResponse = onResponse,
          )
    }
  }
}
