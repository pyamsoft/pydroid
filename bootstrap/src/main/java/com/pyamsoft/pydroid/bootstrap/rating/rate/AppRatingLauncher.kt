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

package com.pyamsoft.pydroid.bootstrap.rating.rate

import android.app.Activity
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.core.ResultWrapper

/** In-App review launcher */
public interface AppRatingLauncher {

  /** Possibly launch an in-app review, not guaranteed due to implementation details */
  @CheckResult public suspend fun rate(activity: Activity): ResultWrapper<Unit>

  public companion object {

    /** Create a no-op rating launcher */
    @JvmStatic
    @CheckResult
    public fun empty(): AppRatingLauncher {
      return object : AppRatingLauncher {
        override suspend fun rate(activity: Activity): ResultWrapper<Unit> {
          return ResultWrapper.success(Unit)
        }
      }
    }
  }
}
