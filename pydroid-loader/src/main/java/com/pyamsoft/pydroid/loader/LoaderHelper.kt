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

import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.loader.loaded.Loaded

object LoaderHelper {

  @JvmOverloads
  @JvmStatic
  @CheckResult
  fun unload(entry: Loaded?,
      defaultLoaded: Loaded = empty()): Loaded {
    if (entry == null) {
      return defaultLoaded
    }

    if (!entry.isUnloaded) {
      entry.unload()
    }
    return defaultLoaded
  }

  @JvmStatic
  @CheckResult
  fun empty(): Loaded {
    return object : Loaded {

      private var unloaded = false

      override val isUnloaded: Boolean
        get() = unloaded

      override fun unload() {
        unloaded = true
      }
    }
  }
}
