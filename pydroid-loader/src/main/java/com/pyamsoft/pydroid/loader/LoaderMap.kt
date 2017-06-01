/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.loader

import com.pyamsoft.pydroid.loader.loaded.Loaded
import java.util.HashMap

class LoaderMap {

  private val map: MutableMap<String, Loaded> = HashMap()

  /**
   * Puts a new element into the map

   * If an old element exists, its task is cancelled first before adding the new one
   */
  fun put(tag: String, loaded: Loaded) {
    if (map.containsKey(tag)) {
      map.put(tag, LoaderHelper.unload(map[tag]))
    }
    map.put(tag, loaded)
  }

  /**
   * Clear all elements in the map

   * If the elements have not been cancelled yet, cancel them before removing them
   */
  fun clear() {
    map.apply {
      entries.forEach { it.setValue(LoaderHelper.unload(it.value)) }
      clear()
    }
  }
}
