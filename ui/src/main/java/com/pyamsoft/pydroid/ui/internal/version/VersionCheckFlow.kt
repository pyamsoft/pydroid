/*
 * Copyright 2020 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.internal.version

import com.pyamsoft.pydroid.arch.UiControllerEvent
import com.pyamsoft.pydroid.arch.UiViewEvent
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.bootstrap.version.AppUpdateLauncher

internal data class VersionCheckViewState
internal constructor(
    val isLoading: Boolean,
    val throwable: Throwable?,
    val navigationError: Throwable?,
) : UiViewState

internal sealed class VersionCheckViewEvent : UiViewEvent {

  object LoadingHidden : VersionCheckViewEvent()

  object ErrorHidden : VersionCheckViewEvent()

  object NavigationHidden : VersionCheckViewEvent()
}

internal sealed class VersionCheckControllerEvent : UiControllerEvent {

  data class LaunchUpdate
  internal constructor(val isFallbackEnabled: Boolean, val launcher: AppUpdateLauncher) :
      VersionCheckControllerEvent()

  object UpgradeReady : VersionCheckControllerEvent()
}
