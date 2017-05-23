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

package com.pyamsoft.pydroid.ui

import com.pyamsoft.pydroid.about.Licenses

internal class UiLicenses private constructor() {

  init {
    throw RuntimeException("No instances")
  }

  private class Names private constructor() {

    init {
      throw RuntimeException("No instances")
    }

    companion object {

      internal val LEAK_CANARY = "Leak Canary"
      internal val FAST_ADAPTER = "Fast Adapter"
    }
  }

  private class HomepageUrls private constructor() {

    init {
      throw RuntimeException("No instances")
    }

    companion object {

      internal val LEAK_CANARY = "https://github.com/square/leakcanary"
      internal val FAST_ADAPTER = "https://github.com/mikepenz/FastAdapter"
    }
  }

  private class LicenseLocations private constructor() {

    init {
      throw RuntimeException("No instances")
    }

    companion object {

      internal val LEAK_CANARY = Licenses.LicenseLocations._BASE + "leakcanary"
      internal val FAST_ADAPTER = Licenses.LicenseLocations._BASE + "fastadapter"
    }
  }

  companion object {

    fun addLicenses() {
      Licenses.create(Names.LEAK_CANARY, HomepageUrls.LEAK_CANARY, LicenseLocations.LEAK_CANARY)
      Licenses.create(Names.FAST_ADAPTER, HomepageUrls.FAST_ADAPTER, LicenseLocations.FAST_ADAPTER)
    }
  }
}
