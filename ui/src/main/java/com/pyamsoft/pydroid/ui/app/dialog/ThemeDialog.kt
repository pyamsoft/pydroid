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

package com.pyamsoft.pydroid.ui.app.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import com.pyamsoft.pydroid.ui.app.noTitle
import com.pyamsoft.pydroid.util.asDp
import com.pyamsoft.pydroid.util.valueFromCurrentTheme
import kotlin.LazyThreadSafetyMode.NONE

abstract class ThemeDialog protected constructor() : DialogFragment() {

    private val themeFromAttrs: Int by lazy(NONE) {
        requireActivity().valueFromCurrentTheme(com.google.android.material.R.attr.dialogTheme)
    }

    private fun applyDialogMargins(view: View) {
        val margin = 16.asDp(view.context)
        val params = view.layoutParams
        if (params == null) {
            view.layoutParams = ViewGroup.MarginLayoutParams(margin, margin)
        } else {
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = margin
                bottomMargin = margin
                marginStart = margin
                marginEnd = margin
            }
        }
    }

    final override fun getTheme(): Int {
        return themeFromAttrs
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(ContextThemeWrapper(requireActivity(), theme), theme).noTitle()
    }

    @CallSuper
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        applyDialogMargins(view)
    }
}
