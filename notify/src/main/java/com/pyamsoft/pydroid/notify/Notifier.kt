package com.pyamsoft.pydroid.notify

import android.app.NotificationManager
import android.content.Context
import androidx.annotation.CheckResult
import androidx.core.content.getSystemService

class Notifier(private val dispatchers: Set<NotifyDispatcher<*>>, context: Context) {

    private val manager by lazy {
        requireNotNull(context.applicationContext.getSystemService<NotificationManager>())
    }

    @CheckResult
    @JvmOverloads
    fun <T : NotifyData> show(
        id: NotifyId = generateNotificationId(),
        tag: NotifyTag = NOTIFY_EMPTY_TAG,
        notification: T
    ): NotifyId {
        val dispatcher = dispatchers
            .asSequence()
            .filter { it.canShow(notification) }
            .map {
                // Unsafe cast but should be fine because of the above filter clause.
                // If the dispatcher canShow function returns false truths, then this will break
                // but that's on you.
                @Suppress("UNCHECKED_CAST")
                return@map it as? NotifyDispatcher<T>
            }
            .firstOrNull()
            ?: throw MissingDispatcherException(dispatchers, notification)

        val newNotification = dispatcher.build(notification)

        if (tag.tag.isNotBlank()) {
            manager.notify(tag.tag, id.id, newNotification)
        } else {
            manager.notify(id.id, newNotification)
        }

        return id
    }

    @JvmOverloads
    fun cancel(
        tag: NotifyTag = NOTIFY_EMPTY_TAG,
        id: NotifyId
    ) {
        if (tag.tag.isNotBlank()) {
            manager.cancel(tag.tag, id.id)
        } else {
            manager.cancel(id.id)
        }
    }

    companion object {

        private val NOTIFY_EMPTY_TAG = "".asNotifyTag()
        private val NOTIFICATION_ID_RANGE = (1000..50000)

        @CheckResult
        private fun generateNotificationId(): NotifyId {
            val rawId = NOTIFICATION_ID_RANGE.random()
            return rawId.asNotifyId()
        }
    }
}
