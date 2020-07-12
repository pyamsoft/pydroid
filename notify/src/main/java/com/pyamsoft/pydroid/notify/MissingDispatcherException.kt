package com.pyamsoft.pydroid.notify

class MissingDispatcherException internal constructor(
    dispatchers: Set<NotifyDispatcher<*>>,
    notification: NotifyData
) : IllegalArgumentException(
    """
    No dispatcher available to handle notification: $notification
    Available dispatchers: $dispatchers
    """.trimIndent()
)
