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

package com.pyamsoft.pydroid.ui.about.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.CheckResult
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.R.layout
import com.pyamsoft.pydroid.ui.app.noTitle
import com.pyamsoft.pydroid.ui.app.requireArguments
import com.pyamsoft.pydroid.util.hyperlink

class ViewUrlDialog : DialogFragment(), UrlUiComponent.Callback, UrlToolbarUiComponent.Callback {

  internal lateinit var toolbarComponent: UrlToolbarUiComponent
  internal lateinit var component: UrlUiComponent

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return super.onCreateDialog(savedInstanceState)
        .noTitle()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(layout.layout_constraint, container, false)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    val name = requireArguments().getString(NAME, "")
    val link = requireArguments().getString(LINK, "")
    require(name.isNotBlank())
    require(link.isNotBlank())

    val layoutRoot = view.findViewById<ConstraintLayout>(R.id.layout_constraint)
    PYDroid.obtain(view.context.applicationContext)
        .plusViewLicenseComponent(viewLifecycleOwner, layoutRoot, link, name)
        .inject(this)

    component.bind(viewLifecycleOwner, savedInstanceState, this)
    toolbarComponent.bind(viewLifecycleOwner, savedInstanceState, this)

    toolbarComponent.layout(layoutRoot)
    component.layout(layoutRoot, toolbarComponent.id())
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    toolbarComponent.saveState(outState)
    component.saveState(outState)
  }

  override fun onResume() {
    super.onResume()
    // The dialog is super small for some reason. We have to set the size manually, in onResume
    dialog.window?.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT
    )
  }

  override fun onCancelViewing() {
    dismiss()
  }

  override fun onNavigateToExternalUrl(url: String) {
    val error = url.hyperlink(requireContext())
        .navigate()

    if (error != null) {
      component.navigationFailed(error)
    }

    // Dismiss the dialog
    dismiss()
  }

  companion object {

    internal const val TAG = "ViewUrlDialog"
    private const val NAME = "name"
    private const val LINK = "link"

    @CheckResult
    @JvmStatic
    fun newInstance(
      name: String,
      link: String
    ): ViewUrlDialog {
      return ViewUrlDialog().apply {
        arguments = Bundle().apply {
          putString(NAME, name)
          putString(LINK, link)
        }
      }
    }
  }
}
