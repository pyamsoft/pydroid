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

package com.pyamsoft.pydroid.ui.internal.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.ViewCompat
import com.google.android.material.R as R2
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.makeFullWidth
import com.pyamsoft.pydroid.ui.databinding.ChangelogDialogBinding
import com.pyamsoft.pydroid.util.asDp

internal abstract class IconDialog : AppCompatDialogFragment() {

  private val themedContext by lazy {
    // Load the original dialog theme into the "rest" of the dialog content
    val dialogTheme =
        TypedValue().run {
          requireActivity().theme.resolveAttribute(R2.attr.dialogTheme, this, true)
          return@run resourceId
        }

    ContextThemeWrapper(requireActivity(), dialogTheme)
  }

  override fun getContext(): Context? {
    return if (activity == null) super.getContext() else themedContext
  }

  final override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View? {
    val newInflater = inflater.cloneInContext(themedContext)
    return newInflater.inflate(R.layout.changelog_dialog, container, false)
  }

  final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    makeFullWidth()

    val binding = ChangelogDialogBinding.bind(view)
    ViewCompat.setElevation(binding.changelogIcon, 8.asDp(view.context).toFloat())
    onBindingCreated(binding, savedInstanceState)
  }

  final override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return AppCompatDialog(requireActivity(), R.style.ThemeOverlay_PYDroid_Changelog)
  }

  protected abstract fun onBindingCreated(
      binding: ChangelogDialogBinding,
      savedInstanceState: Bundle?
  )
}
