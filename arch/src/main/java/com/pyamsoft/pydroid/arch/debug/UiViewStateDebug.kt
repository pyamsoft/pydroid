package com.pyamsoft.pydroid.arch.debug

import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.core.RemoveInRelease

object UiViewStateDebug {

    /**
     * If we are in debug mode, perform the state change twice and make sure that it produces
     * the same state both times.
     */
    @JvmStatic
    @RemoveInRelease
    fun <S : UiViewState> checkStateEquality(state1: S, state2: S) {
        if (state1 != state2) {
            // Pull a page from the MvRx repo's BaseMvRxViewModel :)
            val changedProp = state1::class.java.declaredFields.asSequence()
                .onEach { it.isAccessible = true }
                .firstOrNull { property ->
                    try {
                        val prop1 = property.get(state1)
                        val prop2 = property.get(state2)
                        prop1 != prop2
                    } catch (e: Throwable) {
                        // Failed but we don't care
                        false
                    }
                }

            if (changedProp == null) {
                throw DeterministicStateError(state1, state2, null)
            } else {
                val prop1 = changedProp.get(state1)
                val prop2 = changedProp.get(state2)
                throw DeterministicStateError(prop1, prop2, changedProp.name)
            }
        }
    }

    private class DeterministicStateError(
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

}