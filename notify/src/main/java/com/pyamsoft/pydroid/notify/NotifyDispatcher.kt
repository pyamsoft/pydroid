package com.pyamsoft.pydroid.notify

import android.app.Notification
import androidx.annotation.CheckResult

interface NotifyDispatcher<T : NotifyData> {

    @CheckResult
    fun canShow(notification: NotifyData): Boolean

    @CheckResult
    fun build(notification: T): Notification
}