package com.pyamsoft.pydroid.notify

import androidx.annotation.CheckResult

data class NotifyId internal constructor(val id: Int)

@CheckResult
fun Int.asNotifyId(): NotifyId {
    return NotifyId(this)
}