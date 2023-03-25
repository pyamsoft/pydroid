/*
 * Copyright 2023 pyamsoft
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

package com.pyamsoft.pydroid.bus

/** A simple receive side implementation of an EventBus */
public interface EventConsumer<T : Any> {

  /**
   * Receive an event from the bus and do things with it.
   *
   * Any events sent before this function is called may be replayed or dropped based on the
   * implementation specifics.
   */
  public suspend fun onEvent(emitter: suspend (event: T) -> Unit)
}
