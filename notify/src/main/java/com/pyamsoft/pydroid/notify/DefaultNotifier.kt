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

import android.app.Notification
import android.app.Service
import android.content.Context
import androidx.annotation.CheckResult
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat

internal class DefaultNotifier
internal constructor(
    private val dispatchers: Set<NotifyDispatcher<*>>,
    context: Context,
) : Notifier {

  private val manager by lazy { NotificationManagerCompat.from(context.applicationContext) }

  override fun <T : NotifyData> show(channelInfo: NotifyChannelInfo, notification: T): NotifyId {
    return show(generateNotificationId(), NOTIFY_EMPTY_TAG, channelInfo, notification)
  }

  override fun <T : NotifyData> show(
      id: NotifyId,
      channelInfo: NotifyChannelInfo,
      notification: T
  ): NotifyId {
    return show(id, NOTIFY_EMPTY_TAG, channelInfo, notification)
  }

  override fun <T : NotifyData> show(
      tag: NotifyTag,
      channelInfo: NotifyChannelInfo,
      notification: T
  ): NotifyId {
    return show(generateNotificationId(), tag, channelInfo, notification)
  }

  @CheckResult
  private fun <T : NotifyData> buildNotification(
      id: NotifyId,
      channelInfo: NotifyChannelInfo,
      notification: T
  ): Notification {
    val dispatcher =
        dispatchers
            .asSequence()
            .filter { it.canShow(notification) }
            .map {
              // Unsafe cast but should be fine because of the above filter clause.
              // If the dispatcher canShow function returns false truths, then this will break
              // but that's on you.
              @Suppress("UNCHECKED_CAST") return@map it as? NotifyDispatcher<T>
            }
            .firstOrNull()
            ?: throw MissingDispatcherException(dispatchers, notification)

    return dispatcher.build(id, channelInfo, notification)
  }

  override fun <T : NotifyData> show(
      id: NotifyId,
      tag: NotifyTag,
      channelInfo: NotifyChannelInfo,
      notification: T
  ): NotifyId {
    val newNotification = buildNotification(id, channelInfo, notification)
    if (tag.tag.isNotBlank()) {
      manager.notify(tag.tag, id.id, newNotification)
    } else {
      manager.notify(id.id, newNotification)
    }

    return id
  }

  override fun cancel(id: NotifyId) {
    cancel(id, NOTIFY_EMPTY_TAG)
  }

  override fun cancel(id: NotifyId, tag: NotifyTag) {
    if (tag.tag.isNotBlank()) {
      manager.cancel(tag.tag, id.id)
    } else {
      manager.cancel(id.id)
    }
  }

  override fun <T : NotifyData> startForeground(
      service: Service,
      channelInfo: NotifyChannelInfo,
      notification: T
  ): NotifyId {
    return startForeground(service, generateNotificationId(), channelInfo, notification)
  }

  override fun <T : NotifyData> startForeground(
      service: Service,
      id: NotifyId,
      channelInfo: NotifyChannelInfo,
      notification: T
  ): NotifyId {
    val newNotification = buildNotification(id, channelInfo, notification)
    service.startForeground(id.id, newNotification)
    return id
  }

  override fun stopForeground(service: Service, id: NotifyId) {
    stopForeground(service, id, NOTIFY_EMPTY_TAG)
  }

  override fun stopForeground(service: Service, id: NotifyId, tag: NotifyTag) {
    ServiceCompat.stopForeground(service, ServiceCompat.STOP_FOREGROUND_REMOVE)
    cancel(id, tag)
  }

  companion object {

    private val NOTIFY_EMPTY_TAG = "".toNotifyTag()
    private val NOTIFICATION_ID_RANGE = (1000..50000)

    @CheckResult
    private fun generateNotificationId(): NotifyId {
      val rawId = NOTIFICATION_ID_RANGE.random()
      return rawId.toNotifyId()
    }
  }
}
