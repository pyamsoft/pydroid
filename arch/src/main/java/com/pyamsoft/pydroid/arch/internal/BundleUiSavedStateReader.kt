/*
 * Copyright 2022 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.arch.internal

import android.os.Bundle
import com.pyamsoft.pydroid.arch.UiSavedStateReader

/** Bundle backed implementation of a UiSavedStateReader */
@Deprecated("Start migrating over to consumeRestoredState")
@PublishedApi
internal class BundleUiSavedStateReader
@PublishedApi
internal constructor(private val bundle: Bundle?) : UiSavedStateReader {

  override fun <T : Any> get(key: String): T? {
    // This is Deprecated but it still works, and no other API really replaces it
    @Suppress("UNCHECKED_CAST", "DEPRECATION") return bundle?.get(key) as? T
  }

  override fun all(): Map<String, *> {
    val b = bundle ?: return EMPTY_MAP
    val keys = b.keySet()

    val result = mutableMapOf<String, Any?>()
    for (key in keys) {
      // This is Deprecated but it still works, and no other API really replaces it
      @Suppress("DEPRECATION") val value = b.get(key)
      result[key] = value
    }

    return result
  }

  companion object {
    private val EMPTY_MAP = emptyMap<String, Any?>()
  }
}
