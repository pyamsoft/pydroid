/*
 * Copyright 2026 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.bootstrap.play.version

import com.pyamsoft.pydroid.bootstrap.play.version.fake.FakeAppUpdater
import com.pyamsoft.pydroid.bootstrap.play.version.play.PlayStoreAppUpdater
import com.pyamsoft.pydroid.bootstrap.version.VersionModule
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdater

public class PlayVersionModule(params: Parameters) : VersionModule(params) {

  override fun isLive(): Boolean = true

  override fun newUpdater(params: Parameters): AppUpdater {
    val fake = params.fakeUpgradeRequest
    return if (fake != null) {
      FakeAppUpdater(
          enforcer = params.enforcer,
          context = params.context.applicationContext,
          version = params.version,
          dispatchers = params.dispatchers,
          fakeUpgradeRequest = fake,
      )
    } else {
      PlayStoreAppUpdater(
          enforcer = params.enforcer,
          context = params.context.applicationContext,
          dispatchers = params.dispatchers,
      )
    }
  }
}
