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

package com.pyamsoft.pydroid.cache

import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.data.clear
import io.reactivex.Observable
import io.reactivex.disposables.Disposables
import timber.log.Timber
import java.util.concurrent.TimeUnit.MILLISECONDS

class CacheTimeout @JvmOverloads constructor(
  private val cache: Cache,
  tag: String = ""
) {

  private var disposable = Disposables.empty()
  private val logTag: String = generateTag(tag)

  private fun clear() {
    disposable = disposable.clear()
  }

  @CheckResult
  private fun generateTag(tag: String): String {
    if (tag.isNotBlank()) {
      return tag
    } else {
      return cache::class.java.simpleName
    }
  }

  fun reset() {
    Timber.d("Reset cache timeout for $logTag")
    clear()
  }

  fun queue() {
    clear()

    Timber.d("Queue cache timeout for: $logTag")
    disposable = Observable.interval(DEFAULT_CACHE_TIMEOUT, MILLISECONDS)
        .subscribe({
          Timber.d("Clear cache on timeout: $logTag")
          cache.clearCache()
        }, {
          Timber.e(it, "Error clearing on timeout: $logTag")
        })
  }
}

