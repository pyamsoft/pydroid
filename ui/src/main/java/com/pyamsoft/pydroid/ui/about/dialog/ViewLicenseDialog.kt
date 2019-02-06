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
import com.pyamsoft.pydroid.ui.about.dialog.LicenseStateEvent.Complete
import com.pyamsoft.pydroid.ui.about.dialog.LicenseStateEvent.Loading
import com.pyamsoft.pydroid.ui.about.dialog.LicenseViewEvent.ToolbarMenuClick
import com.pyamsoft.pydroid.ui.about.dialog.LicenseViewEvent.ToolbarNavClick
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarDialog
import com.pyamsoft.pydroid.ui.app.fragment.requireArguments
import com.pyamsoft.pydroid.ui.arch.destroy
import com.pyamsoft.pydroid.util.hyperlink

internal class ViewLicenseDialog : ToolbarDialog() {

  internal lateinit var toolbarComponent: LicenseToolbarUiComponent
  internal lateinit var webviewComponent: LicenseWebviewUiComponent
  internal lateinit var dropshadowComponent: DropshadowUiComponent
  internal lateinit var loadingComponent: SpinnerUiComponent<LicenseStateEvent, Loading, Complete>
  internal lateinit var worker: ViewLicenseWorker

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val name = requireArguments().getString(NAME, "")
    val link = requireArguments().getString(LINK, "")
    require(name.isNotBlank())
    require(link.isNotBlank())

    val root = inflater.inflate(R.layout.layout_constraint, container, false)

    PYDroid.obtain(requireContext())
        .plusViewLicenseComponent(viewLifecycleOwner, root as ViewGroup, link, name)
        .inject(this)

    return root
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    worker.onLoadErrorEvent { dismiss() }
        .destroy(viewLifecycleOwner)

    toolbarComponent.onUiEvent {
      return@onUiEvent when (it) {
        is ToolbarNavClick -> dismiss()
        is ToolbarMenuClick -> onToolbarMenuItemClicked(it.itemId, it.link)
      }
    }
        .destroy(viewLifecycleOwner)

    toolbarComponent.create(savedInstanceState)
    webviewComponent.create(savedInstanceState)
    loadingComponent.create(savedInstanceState)
    dropshadowComponent.create(savedInstanceState)

    applyConstraints(view as ConstraintLayout)

    worker.loadUrl()
  }

  private fun onToolbarMenuItemClicked(
    itemId: Int,
    link: String
  ) {
    when (itemId) {
      R.id.menu_item_view_license -> {
        val error = link.hyperlink(requireContext())
            .navigate()
        if (error != null) {
          worker.failedViewExternal(error)
          dismiss()
        }
      }
    }
  }

  private fun applyConstraints(layoutRoot: ConstraintLayout) {
    ConstraintSet().apply {
      clone(layoutRoot)

      toolbarComponent.also {
        connect(it.id(), ConstraintSet.TOP, layoutRoot.id, ConstraintSet.TOP)
        connect(it.id(), ConstraintSet.START, layoutRoot.id, ConstraintSet.START)
        connect(it.id(), ConstraintSet.END, layoutRoot.id, ConstraintSet.END)
        constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
      }

      dropshadowComponent.also {
        connect(it.id(), ConstraintSet.TOP, toolbarComponent.id(), ConstraintSet.BOTTOM)
        connect(it.id(), ConstraintSet.START, layoutRoot.id, ConstraintSet.START)
        connect(it.id(), ConstraintSet.END, layoutRoot.id, ConstraintSet.END)
        constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
      }

      webviewComponent.also {
        connect(it.id(), ConstraintSet.TOP, toolbarComponent.id(), ConstraintSet.BOTTOM)
        connect(it.id(), ConstraintSet.START, layoutRoot.id, ConstraintSet.START)
        connect(it.id(), ConstraintSet.END, layoutRoot.id, ConstraintSet.END)
        connect(it.id(), ConstraintSet.BOTTOM, layoutRoot.id, ConstraintSet.BOTTOM)
        constrainHeight(it.id(), ConstraintSet.MATCH_CONSTRAINT)
        constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
      }

      loadingComponent.also {
        connect(it.id(), ConstraintSet.TOP, toolbarComponent.id(), ConstraintSet.BOTTOM)
        connect(it.id(), ConstraintSet.START, layoutRoot.id, ConstraintSet.START)
        connect(it.id(), ConstraintSet.END, layoutRoot.id, ConstraintSet.END)
        connect(it.id(), ConstraintSet.BOTTOM, layoutRoot.id, ConstraintSet.BOTTOM)
        constrainHeight(it.id(), ConstraintSet.MATCH_CONSTRAINT)
        constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
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
