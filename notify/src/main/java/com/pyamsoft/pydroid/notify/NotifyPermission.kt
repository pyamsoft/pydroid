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

import androidx.annotation.CheckResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/** Handles permission requesting related to notifications */
public interface NotifyPermission {

  /** Request notification permission from an Activity */
  @CheckResult
  public fun registerRequester(
      activity: FragmentActivity,
      onResponse: (Boolean) -> Unit,
  ): Requester

  /** Request notification permission from a Fragment */
  @CheckResult
  public fun registerRequester(
      fragment: Fragment,
      onResponse: (Boolean) -> Unit,
  ): Requester

  public interface Requester {

    /** Request permission from the underlying ActivityResultContract launcher */
    public fun requestPermissions()

    /** Unregister the underlying ActivityResultContract launcher */
    public fun unregister()
  }

  public companion object {

    /** Create a new instance of a default NotifyPermission */
    @CheckResult
    public fun createDefault(): NotifyPermission {
      return DefaultNotifyPermission()
    }
  }
}
