/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.base.about

import com.pyamsoft.pydroid.cache.Cache
import com.pyamsoft.pydroid.cache.CacheTimeout
import com.pyamsoft.pydroid.cache.TimedEntry
import io.reactivex.Observable

internal class AboutLibrariesInteractorCache internal constructor(
  private val impl: AboutLibrariesInteractor
) : AboutLibrariesInteractor, Cache {

  private val cachedTimeout = CacheTimeout(this)
  private val cachedLicenses = TimedEntry<Observable<AboutLibrariesModel>>()

  override fun loadLicenses(force: Boolean): Observable<AboutLibrariesModel> {
    return cachedLicenses.getElseFresh(force) {
      impl.loadLicenses(true)
          .cache()
    }
        .doOnError { clearCache() }
        .doAfterTerminate { cachedTimeout.queue() }
  }

  override fun clearCache() {
    cachedLicenses.clearCache()
    cachedTimeout.reset()
  }
}
