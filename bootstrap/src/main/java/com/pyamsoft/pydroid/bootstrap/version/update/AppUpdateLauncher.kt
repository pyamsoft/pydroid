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

import android.app.Activity
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.util.ResultWrapper
import org.jetbrains.annotations.TestOnly

/** In app update launcher */
public interface AppUpdateLauncher {

  /** Get the available update version */
  @CheckResult public fun availableUpdateVersion(): Int

  /** Begin an update */
  @CheckResult public suspend fun update(activity: Activity, requestCode: Int): ResultWrapper<Unit>

  public companion object {

    /** Create a no-op update launcher */
    @JvmStatic
    @CheckResult
    public fun empty(): AppUpdateLauncher {
      return object : AppUpdateLauncher {

        override fun availableUpdateVersion(): Int = 0

        override suspend fun update(activity: Activity, requestCode: Int): ResultWrapper<Unit> {
          return ResultWrapper.success(Unit)
        }
      }
    }

    /** Create a test update launcher */
    @TestOnly
    @JvmStatic
    @CheckResult
    public fun test(versionCode: Int): AppUpdateLauncher {
      return object : AppUpdateLauncher {

        override fun availableUpdateVersion(): Int = versionCode

        override suspend fun update(activity: Activity, requestCode: Int): ResultWrapper<Unit> {
          return ResultWrapper.success(Unit)
        }
      }
    }
  }
}
