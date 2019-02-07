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

import android.app.Dialog
import android.os.Bundle
import android.text.SpannedString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.CheckResult
import androidx.fragment.app.DialogFragment
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.noTitle
import com.pyamsoft.pydroid.ui.app.requireArguments
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationPresenter
import com.pyamsoft.pydroid.ui.rating.ChangeLogProvider
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogPresenter.Callback
import com.pyamsoft.pydroid.ui.util.MarketLinker

class RatingDialog : DialogFragment(), Callback {

  internal lateinit var presenter: RatingDialogPresenter
  internal lateinit var failedNavigationPresenter: FailedNavigationPresenter
  internal lateinit var iconView: RatingIconView
  internal lateinit var changelogView: RatingChangelogView
  internal lateinit var controlsView: RatingControlsView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    isCancelable = false
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return super.onCreateDialog(savedInstanceState)
        .noTitle()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val root = inflater.inflate(R.layout.layout_linear_vertical, container, false)

    val rateLink = requireArguments().getString(RATE_LINK, "")
    val changeLogIcon = requireArguments().getInt(CHANGE_LOG_ICON, 0)
    val changelog = requireArguments().getCharSequence(CHANGE_LOG_TEXT, "") as? SpannedString

    requireNotNull(rateLink)
    requireNotNull(changelog)

    require(rateLink.isNotBlank())
    require(changeLogIcon > 0)
    require(changelog.isNotBlank())

    PYDroid.obtain(root.context.applicationContext)
        .plusRatingDialogComponent(
            viewLifecycleOwner, root as ViewGroup, rateLink, changeLogIcon, changelog
        )
        .inject(this)

    return root
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    iconView.inflate(savedInstanceState)
    changelogView.inflate(savedInstanceState)
    controlsView.inflate(savedInstanceState)

    presenter.bind(this)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    iconView.saveState(outState)
    changelogView.saveState(outState)
    controlsView.saveState(outState)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    iconView.teardown()
    changelogView.teardown()
    controlsView.teardown()
  }

  override fun onResume() {
    super.onResume()
    // The dialog is super small for some reason. We have to set the size manually, in onResume
    dialog.window?.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT
    )
  }

  override fun onVisitApplicationPageToRate(packageName: String) {
    val error = MarketLinker.linkToMarketPage(requireContext(), packageName)
    dismiss()

    if (error != null) {
      failedNavigationPresenter.failedNavigation(error)
    }
  }

  override fun onDidNotRate() {
    dismiss()
  }

  companion object {

    internal const val TAG = "RatingDialog"
    private const val CHANGE_LOG_TEXT = "change_log_text"
    private const val CHANGE_LOG_ICON = "change_log_icon"
    private const val RATE_LINK = "rate_link"

    @CheckResult
    @JvmStatic
    fun newInstance(provider: ChangeLogProvider): RatingDialog {
      return RatingDialog().apply {
        arguments = Bundle().apply {
          putString(RATE_LINK, provider.getPackageName())
          putCharSequence(CHANGE_LOG_TEXT, provider.changelog)
          putInt(CHANGE_LOG_ICON, provider.applicationIcon)
        }
      }
    }
  }
}
