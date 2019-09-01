/*
 * Copyright 2019 Peter Kenji Yamanaka
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
 *
 */

package com.pyamsoft.pydroid.ui.privacy

import com.pyamsoft.pydroid.arch.EventBus

object PrivacyEventBus : EventBus<PrivacyEvents> {

    private val bus by lazy { EventBus.create<PrivacyEvents>() }

    override fun publish(event: PrivacyEvents) {
        bus.publish(event)
    }

    override suspend fun send(event: PrivacyEvents) {
        bus.send(event)
    }

    override suspend fun onEvent(emitter: suspend (event: PrivacyEvents) -> Unit) {
        bus.onEvent(emitter)
    }
}
