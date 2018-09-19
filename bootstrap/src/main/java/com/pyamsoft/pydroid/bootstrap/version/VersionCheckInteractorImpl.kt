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

package com.pyamsoft.pydroid.bootstrap.version

import com.popinnow.android.repo.Repo
import com.pyamsoft.pydroid.bootstrap.CacheKeys
import com.pyamsoft.pydroid.core.cache.Cache
import com.pyamsoft.pydroid.core.threads.Enforcer
import io.reactivex.Single

internal class VersionCheckInteractorImpl internal constructor(
  private val enforcer: Enforcer,
  private val network: VersionCheckInteractor,
  private val repo: Repo
) : VersionCheckInteractor, Cache {

  override fun checkVersion(
    bypass: Boolean,
    packageName: String
  ): Single<Int> {
    return repo.get(bypass, CacheKeys.KEY_VERSION_CHECK) {
      enforcer.assertNotOnMainThread()
      return@get network.checkVersion(true, packageName)
    }
  }

  override fun clearCache() {
    repo.invalidate(CacheKeys.KEY_VERSION_CHECK)
  }
}
