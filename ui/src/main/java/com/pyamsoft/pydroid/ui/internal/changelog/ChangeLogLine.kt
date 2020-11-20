package com.pyamsoft.pydroid.ui.internal.changelog

import androidx.annotation.CheckResult

internal data class ChangeLogLine internal constructor(
    val type: Type,
    val line: String
) {

    internal enum class Type {
        BUGFIX, CHANGE, FEATURE
    }
}

@CheckResult
internal fun String.asBugfix(): ChangeLogLine {
    return ChangeLogLine(ChangeLogLine.Type.BUGFIX, this)
}

@CheckResult
internal fun String.asFeature(): ChangeLogLine {
    return ChangeLogLine(ChangeLogLine.Type.FEATURE, this)
}

@CheckResult
internal fun String.asChange(): ChangeLogLine {
    return ChangeLogLine(ChangeLogLine.Type.CHANGE, this)
}
