/*
 * Copyright 2020 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.arch.debug

import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.core.RemoveInRelease

/**
 * Debugging functions for UiViewState processing
 */
internal object UiViewStateDebug {

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

}