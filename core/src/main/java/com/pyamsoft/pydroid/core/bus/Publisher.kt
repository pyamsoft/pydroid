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

package com.pyamsoft.pydroid.core.bus

interface Publisher<T : Any> {

  /**
   * Publish an event to a registered Receiver class
   *
   * The viewBus does not make any restrictions on what type an DataWrapper should be. While events can be
   * mutable, it is recommended to make your DataWrapper object immutable as the viewBus makes no guarantees
   * about the state of the data
   */
  fun publish(event: T)
}
