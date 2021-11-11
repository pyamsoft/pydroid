/*
 * Copyright 2021 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.internal.protection

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.protection.Protection
import com.pyamsoft.pydroid.ui.app.ActivityBase

internal interface ProtectionComponent {

  fun inject(activity: ActivityBase)

  interface Factory {

    @CheckResult fun create(): ProtectionComponent

    data class Parameters
    internal constructor(
        internal val protection: Protection,
    )
  }

  class Impl private constructor(private val params: Factory.Parameters) : ProtectionComponent {

    override fun inject(activity: ActivityBase) {
      activity.protection = params.protection
    }

    internal class FactoryImpl
    internal constructor(
        private val params: Factory.Parameters,
    ) : Factory {

      override fun create(): ProtectionComponent {
        return Impl(params)
      }
    }
  }
}
