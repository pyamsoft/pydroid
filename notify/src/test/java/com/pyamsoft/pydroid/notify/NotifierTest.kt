/*
 * Copyright 2026 pyamsoft
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

import android.app.Notification
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

private data class FakeNotifyData(val text: String) : NotifyData

private class FakeDispatcher : NotifyDispatcher<FakeNotifyData> {

  override fun canShow(notification: NotifyData): Boolean = notification is FakeNotifyData

  override fun build(
      id: NotifyId,
      channelInfo: NotifyChannelInfo,
      notification: FakeNotifyData,
  ): Notification {
    return NotificationCompat.Builder(RuntimeEnvironment.getApplication(), channelInfo.id)
        .setContentText(notification.text)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .build()
  }
}

@RunWith(RobolectricTestRunner::class)
@Config(
    // Need this here since Robolectric does not yet support API 37 (which is default otherwise)
    minSdk = Build.VERSION_CODES.O,
    maxSdk = Build.VERSION_CODES.BAKLAVA,
)
public class NotifierTest {

  private val channelInfo =
      NotifyChannelInfo(id = "channel", title = "Title", description = "Description")

  private fun shadowNotificationManager() =
      shadowOf(
          RuntimeEnvironment.getApplication().getSystemService(NotificationManager::class.java)
      )

  @Test
  public fun show_noDispatcher_throwsMissingDispatcherException() {
    val notifier = Notifier.createDefault(RuntimeEnvironment.getApplication(), emptySet())

    assertFailsWith<MissingDispatcherException> {
      notifier.show(channelInfo, FakeNotifyData("hello"))
    }
  }

  @Test
  public fun show_withDispatcher_postsNotification() {
    val notifier =
        Notifier.createDefault(RuntimeEnvironment.getApplication(), setOf(FakeDispatcher()))

    val id = notifier.show(channelInfo, FakeNotifyData("hello"))

    assertEquals(1, shadowNotificationManager().size())
    assertEquals(
        "hello",
        shadowNotificationManager()
            .getNotification(id.id)
            .extras
            .getCharSequence(Notification.EXTRA_TEXT),
    )
  }

  @Test
  public fun cancel_removesNotification() {
    val notifier =
        Notifier.createDefault(RuntimeEnvironment.getApplication(), setOf(FakeDispatcher()))
    val id = notifier.show(channelInfo, FakeNotifyData("hello"))

    notifier.cancel(id)

    assertEquals(0, shadowNotificationManager().size())
  }

  @Test
  public fun show_withTag_postsUnderTag() {
    val notifier =
        Notifier.createDefault(RuntimeEnvironment.getApplication(), setOf(FakeDispatcher()))
    val tag = "my-tag".toNotifyTag()

    val id = notifier.show(tag, channelInfo, FakeNotifyData("hello"))

    assertEquals(1, shadowNotificationManager().size())
    assertNotNull(shadowNotificationManager().getNotification("my-tag", id.id))
  }
}
