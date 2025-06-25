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

package com.pyamsoft.pydroid.notify

import android.content.Context
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.notify.internal.DefaultNotifyGuard

/** Guards various Notification related APIs */
public interface NotifyGuard {

  /**
   * Can we post notifications?
   *
   * Generally true, checks permission on Android 33+ Does not check if User Settings have
   * notifications switched off
   */
  @CheckResult public fun canPostNotification(): Boolean

  public companion object {

    /** Create a new instance of a default Notifier */
    @CheckResult
    public fun createDefault(context: Context): NotifyGuard {
      return DefaultNotifyGuard(context = context.applicationContext)
    }
  }
}
