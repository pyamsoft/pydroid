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

package com.pyamsoft.pydroid.bootstrap.version

import com.popinnow.android.repo.Repo
import com.pyamsoft.pydroid.bootstrap.version.api.UpdatePayload
import com.pyamsoft.pydroid.core.cache.Cache
import com.pyamsoft.pydroid.core.threads.Enforcer
import io.reactivex.Maybe

internal class VersionCheckInteractorImpl internal constructor(
  private val enforcer: Enforcer,
  private val debug: Boolean,
  private val network: VersionCheckInteractor,
  private val repo: Repo<UpdatePayload>
) : VersionCheckInteractor, Cache {

  override fun checkVersion(force: Boolean): Maybe<UpdatePayload> {
    return repo.get(force) {
      enforcer.assertNotOnMainThread()
      // The actual network facing implementation should always
      // return a valid Single wrapped as a Maybe
      return@get network.checkVersion(true)
          .toSingle()
    }
        .filter { it.currentVersion < it.newVersion || (debug && force) }

  }

  override fun clearCache() {
    repo.clear()
  }
}
