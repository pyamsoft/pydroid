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

import android.text.Spannable
import android.text.SpannedString
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.arch.UiViewModelFactory
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.ui.PYDroidViewModelFactory
import kotlin.reflect.KClass

internal class RatingDialogViewModelFactory internal constructor(
    private val parentFactory: PYDroidViewModelFactory,
    private val changelog: SpannedString,
    private val icon: Int,
    private val ratingModule: RatingModule
) : UiViewModelFactory() {

    override fun <T : UiViewModel<*, *, *>> viewModel(modelClass: KClass<T>): UiViewModel<*, *, *> {
        return when (modelClass) {
            RatingDialogViewModel::class -> RatingDialogViewModel(
                changelog,
                icon,
                ratingModule.provideInteractor()
            )
            else -> parentFactory.exposedViewModel(modelClass)
        }
    }
}
