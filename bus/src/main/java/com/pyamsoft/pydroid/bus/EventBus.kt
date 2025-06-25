/*
 * Copyright 2025 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.bus

import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * A basic EventBus interface with send and receive methods
 *
 * An event bus is a MutableSharedFlow but implements an EventConsumer interface which allows a DI
 * framework like Dagger to easily inject it as a 'resolver' of a dependency
 */
@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
public interface EventBus<T : Any> : EventConsumer<T>, MutableSharedFlow<T>
