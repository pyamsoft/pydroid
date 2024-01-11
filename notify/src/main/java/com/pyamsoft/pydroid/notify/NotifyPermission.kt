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

package com.pyamsoft.pydroid.notify

import android.os.Build
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.util.PermissionRequester

/** Handles permission requesting related to notifications */
public interface NotifyPermission {

  public companion object {

    /** Create a new instance of a default NotifyPermission */
    @CheckResult
    public fun createDefault(): PermissionRequester {
      return if (Build.VERSION.SDK_INT >= 33) {
        PermissionRequester.create(android.Manifest.permission.POST_NOTIFICATIONS)
      } else {
        PermissionRequester.NONE
      }
    }
  }
}
