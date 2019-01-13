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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.CheckResult
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.about.dialog.LicenseViewEvents.ToolbarMenuClick
import com.pyamsoft.pydroid.ui.about.dialog.LicenseViewEvents.ToolbarNavClick
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarDialog
import com.pyamsoft.pydroid.ui.app.fragment.requireArguments
import com.pyamsoft.pydroid.ui.app.fragment.requireView
import com.pyamsoft.pydroid.ui.arch.destroy
import com.pyamsoft.pydroid.ui.databinding.LayoutConstraintBinding
import com.pyamsoft.pydroid.ui.util.navigate
import com.pyamsoft.pydroid.ui.widget.shadow.DropshadowUiComponent
import com.pyamsoft.pydroid.ui.widget.spinner.SpinnerUiComponent
import com.pyamsoft.pydroid.util.hyperlink

internal class ViewLicenseDialog : ToolbarDialog() {

  private lateinit var binding: LayoutConstraintBinding

  internal lateinit var presenter: ViewLicensePresenter
  internal lateinit var toolbarComponent: LicenseToolbarUiComponent
  internal lateinit var loadingComponent: SpinnerUiComponent
  internal lateinit var webviewComponent: LicenseWebviewUiComponent
  internal lateinit var dropshadowComponent: DropshadowUiComponent

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = LayoutConstraintBinding.inflate(inflater, container, false)

    val name = requireArguments().getString(NAME, "")
    val link = requireArguments().getString(LINK, "")
    require(name.isNotBlank())
    require(link.isNotBlank())

    PYDroid.obtain(requireContext())
        .plusViewLicenseComponent(binding.layoutRoot, viewLifecycleOwner, link, name)
        .inject(this)

    return binding.root
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    presenter.onLoadErrorEvent { dismiss() }
        .destroy(viewLifecycleOwner)

    toolbarComponent.onUiEvent()
        .subscribe {
          when (it) {
            is ToolbarNavClick -> dismiss()
            is ToolbarMenuClick -> onToolbarMenuItemClicked(it.itemId, it.link)
          }
        }
        .destroy(viewLifecycleOwner)

    toolbarComponent.create(savedInstanceState)
    webviewComponent.create(savedInstanceState)
    loadingComponent.create(savedInstanceState)
    dropshadowComponent.create(savedInstanceState)

    applyConstraints(binding.layoutRoot)

    presenter.loadUrl()
  }

  private fun onToolbarMenuItemClicked(
    itemId: Int,
    link: String
  ) {
    when (itemId) {
      R.id.menu_item_view_license -> {
        link.hyperlink(requireContext())
            .navigate(requireView())
      }
    }
  }

  private fun applyConstraints(layoutRoot: ConstraintLayout) {
    ConstraintSet().apply {
      clone(layoutRoot)

      toolbarComponent.also {
        connect(it.id(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        connect(it.id(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        connect(it.id(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
      }

      webviewComponent.also {
        connect(it.id(), ConstraintSet.TOP, toolbarComponent.id(), ConstraintSet.BOTTOM)
        connect(it.id(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        connect(it.id(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        connect(it.id(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
      }

      loadingComponent.also {
        connect(it.id(), ConstraintSet.TOP, toolbarComponent.id(), ConstraintSet.BOTTOM)
        connect(it.id(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        connect(it.id(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        connect(it.id(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
      }

      dropshadowComponent.also {
        connect(it.id(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        connect(it.id(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        connect(it.id(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
      }

      applyTo(layoutRoot)
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    toolbarComponent.saveState(outState)
    webviewComponent.saveState(outState)
    loadingComponent.saveState(outState)
  }

  override fun onResume() {
    super.onResume()
    // The dialog is super small for some reason. We have to set the size manually, in onResume
    dialog.window?.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT
    )
  }

  companion object {

    internal const val TAG = "ViewLicenseDialog"
    private const val NAME = "name"
    private const val LINK = "link"

    @CheckResult
    @JvmStatic
    fun newInstance(
      name: String,
      link: String
    ): ViewLicenseDialog {
      return ViewLicenseDialog().apply {
        arguments = Bundle().apply {
          putString(NAME, name)
          putString(LINK, link)
        }
      }
    }
  }
}
