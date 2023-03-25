/*
 * Copyright 2023 pyamsoft
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

package com.pyamsoft.pydroid.util

import androidx.annotation.CheckResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.pyamsoft.pydroid.util.internal.DefaultPermissionRequester

/** Handles permission requesting */
public interface PermissionRequester {

  /** Request permission from an Activity */
  @CheckResult
  public fun registerRequester(
      activity: FragmentActivity,
      onResponse: (Boolean) -> Unit,
  ): Requester

  /** Request permission from a Fragment */
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

    /** An empty requester than handles no permissions */
    public val NONE: PermissionRequester = create(emptyArray())

    /** Create a new instance of a default PermissionRequester */
    @CheckResult
    public fun create(permission: String): PermissionRequester {
      return create(arrayOf(permission))
    }

    /** Create a new instance of a default PermissionRequester */
    @CheckResult
    public fun create(permissions: Array<String>): PermissionRequester {
      return DefaultPermissionRequester(permissions)
    }
  }
}
