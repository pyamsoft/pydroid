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
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.DialogFragment
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.noTitle
import com.pyamsoft.pydroid.ui.app.requireArguments
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationPresenter
import com.pyamsoft.pydroid.ui.widget.shadow.DropshadowView
import com.pyamsoft.pydroid.ui.widget.spinner.SpinnerView
import com.pyamsoft.pydroid.util.hyperlink

class ViewUrlDialog : DialogFragment(), UrlPresenter.Callback {

  internal lateinit var toolbar: UrlToolbarView
  internal lateinit var webview: UrlWebviewView
  internal lateinit var dropshadow: DropshadowView
  internal lateinit var spinner: SpinnerView
  internal lateinit var presenter: UrlPresenter
  internal lateinit var failedNavigationPresenter: FailedNavigationPresenter

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return super.onCreateDialog(savedInstanceState)
        .noTitle()
  }

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

    toolbar.inflate(savedInstanceState)
    webview.inflate(savedInstanceState)
    spinner.inflate(savedInstanceState)
    dropshadow.inflate(savedInstanceState)

    presenter.bind(this)

    applyConstraints(view as ConstraintLayout)

    // TODO Better arch
    // This looks weird because the webview is the state controller and the view...
    webview.loadUrl()
  }

  private fun applyConstraints(layoutRoot: ConstraintLayout) {
    ConstraintSet().apply {
      clone(layoutRoot)

      toolbar.also {
        connect(it.id(), ConstraintSet.TOP, layoutRoot.id, ConstraintSet.TOP)
        connect(it.id(), ConstraintSet.START, layoutRoot.id, ConstraintSet.START)
        connect(it.id(), ConstraintSet.END, layoutRoot.id, ConstraintSet.END)
        constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
      }

      dropshadow.also {
        connect(it.id(), ConstraintSet.TOP, toolbar.id(), ConstraintSet.BOTTOM)
        connect(it.id(), ConstraintSet.START, layoutRoot.id, ConstraintSet.START)
        connect(it.id(), ConstraintSet.END, layoutRoot.id, ConstraintSet.END)
        constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
      }

      webview.also {
        connect(it.id(), ConstraintSet.TOP, toolbar.id(), ConstraintSet.BOTTOM)
        connect(it.id(), ConstraintSet.START, layoutRoot.id, ConstraintSet.START)
        connect(it.id(), ConstraintSet.END, layoutRoot.id, ConstraintSet.END)
        connect(it.id(), ConstraintSet.BOTTOM, layoutRoot.id, ConstraintSet.BOTTOM)
        constrainHeight(it.id(), ConstraintSet.MATCH_CONSTRAINT)
        constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
      }

      spinner.also {
        connect(it.id(), ConstraintSet.TOP, toolbar.id(), ConstraintSet.BOTTOM)
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
    toolbar.saveState(outState)
    webview.saveState(outState)
    dropshadow.saveState(outState)
    spinner.saveState(outState)
  }

  override fun onResume() {
    super.onResume()
    // The dialog is super small for some reason. We have to set the size manually, in onResume
    dialog.window?.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT
    )
  }

  override fun onDestroyView() {
    super.onDestroyView()
    toolbar.teardown()
    webview.teardown()
    dropshadow.teardown()
    spinner.teardown()
  }

  override fun onWebviewBegin() {
    webview.hide()
    spinner.show()
  }

  override fun onWebviewOtherPageLoaded(url: String) {
    webview.hide()
    spinner.show()
  }

  override fun onWebviewTargetPageLoaded(url: String) {
    webview.hide()
    spinner.show()
  }

  override fun onWebviewExternalNavigationEvent(url: String) {
    navigateAway(url)
  }

  override fun onToolbarNavigateEvent() {
    dismiss()
  }

  override fun onToolbarMenuItemEvent(
    itemId: Int,
    url: String
  ) {
    when (itemId) {
      R.id.menu_item_view_license -> {
        navigateAway(url)
      }
    }
  }

  private fun navigateAway(url: String) {
    val error = url.hyperlink(requireContext())
        .navigate()

    dismiss()
    if (error != null) {
      failedNavigationPresenter.failedNavigation(error)
    }
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
