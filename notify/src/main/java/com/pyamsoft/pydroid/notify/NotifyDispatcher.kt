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

package com.pyamsoft.pydroid.notify

import android.app.Notification
import androidx.annotation.CheckResult

/** A NotifyDispatcher handles a type of NotifyData and shows notifications for that data */
public interface NotifyDispatcher<T : NotifyData> {

  /** Whether or not this dispatcher instance can handle the given notify data */
  @CheckResult public fun canShow(notification: NotifyData): Boolean

  /** Build a notification from the given notify data and channel information */
  @CheckResult
  public fun build(id: NotifyId, channelInfo: NotifyChannelInfo, notification: T): Notification
}
