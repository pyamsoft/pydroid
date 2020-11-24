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

package com.pyamsoft.pydroid.ui.internal.changelog.dialog

import com.pyamsoft.pydroid.arch.UiControllerEvent
import com.pyamsoft.pydroid.arch.UiViewEvent
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogLine

internal data class ChangeLogDialogViewState internal constructor(
    val icon: Int,
    val name: CharSequence,
    val changeLog: List<ChangeLogLine>,
) : UiViewState

internal sealed class ChangeLogDialogViewEvent : UiViewEvent {

    object Close : ChangeLogDialogViewEvent()
}

internal sealed class ChangeLogDialogControllerEvent : UiControllerEvent {

    object Close : ChangeLogDialogControllerEvent()
}
