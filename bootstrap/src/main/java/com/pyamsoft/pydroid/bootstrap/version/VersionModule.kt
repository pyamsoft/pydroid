/*
 * Copyright 2025 pyamsoft
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

package com.pyamsoft.pydroid.bootstrap.version

import android.content.Context
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.version.play.PlayStoreAppUpdater
import com.pyamsoft.pydroid.core.ThreadEnforcer

/** In-App update module */
public class VersionModule(params: Parameters) {

  private val impl: VersionInteractorImpl

  init {
    val updater =
        PlayStoreAppUpdater(
            enforcer = params.enforcer,
            context = params.context.applicationContext,
            version = params.version,
            isFakeUpgradeAvailable = params.isFakeUpgradeAvailable,
        )

    val network = VersionInteractorNetwork(updater)
    impl = VersionInteractorImpl(network)
  }

  /** Provide version interactor */
  @CheckResult
  public fun provideInteractor(): VersionInteractor {
    return impl
  }

  /** Module parameters */
  public data class Parameters
  @JvmOverloads
  public constructor(
      internal val context: Context,
      internal val version: Int,
      internal val enforcer: ThreadEnforcer,

      /** If this field is set, the version module will always deliver an update */
      internal val isFakeUpgradeAvailable: Boolean = false,
  )
}
