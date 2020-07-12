package com.pyamsoft.pydroid.notify

import androidx.annotation.CheckResult

data class NotifyTag internal constructor(val tag: String)

@CheckResult
fun String.asNotifyTag(): NotifyTag {
    return NotifyTag(this)
}
