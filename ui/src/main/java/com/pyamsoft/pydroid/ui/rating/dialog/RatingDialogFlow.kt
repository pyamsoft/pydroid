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

package com.pyamsoft.pydroid.ui.rating.dialog

import com.pyamsoft.pydroid.arch.UiControllerEvent
import com.pyamsoft.pydroid.arch.UiViewEvent
import com.pyamsoft.pydroid.arch.UiViewState

data class RatingDialogViewState internal constructor(val throwable: Throwable?) : UiViewState

sealed class RatingDialogViewEvent : UiViewEvent {

  data class Rate internal constructor(val link: String) : RatingDialogViewEvent()

  object Cancel : RatingDialogViewEvent()

}

sealed class RatingDialogControllerEvent : UiControllerEvent {

  data class NavigateRating internal constructor(val link: String) : RatingDialogControllerEvent()

  object CancelDialog : RatingDialogControllerEvent()

}
