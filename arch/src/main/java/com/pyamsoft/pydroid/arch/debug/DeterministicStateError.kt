package com.pyamsoft.pydroid.arch.debug

/**
 * Error when two state objects do not match up
 */
internal class DeterministicStateError internal constructor(
    state1: Any?,
    state2: Any?,
    prop: String?
) : IllegalStateException(
    """State changes must be deterministic
       ${if (prop != null) "Property '$prop' changed:" else ""}
       $state1
       $state2
       """.trimIndent()
)