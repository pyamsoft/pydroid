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

import android.os.Bundle
import android.text.SpannedString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.annotation.CheckResult
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.google.android.material.snackbar.Snackbar
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.tryDispose
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.R2
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarDialog
import com.pyamsoft.pydroid.ui.app.fragment.requireArguments
import com.pyamsoft.pydroid.ui.app.fragment.requireView
import com.pyamsoft.pydroid.ui.arch.destroy
import com.pyamsoft.pydroid.ui.rating.ChangeLogProvider
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogViewEvent.Cancel
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogViewEvent.VisitMarket
import com.pyamsoft.pydroid.ui.util.MarketLinker
import com.pyamsoft.pydroid.ui.util.Snackbreak

internal class RatingDialog : ToolbarDialog() {

  internal lateinit var iconComponent: RatingIconUiComponent
  internal lateinit var changelogComponent: RatingChangelogUiComponent
  internal lateinit var controlsComponent: RatingControlsUiComponent
  internal lateinit var worker: RatingDialogWorker

  private lateinit var unbinder: Unbinder
  @field:BindView(R2.id.layout_root) internal lateinit var layoutRoot: LinearLayout

  private var marketErrorSnackbar: Snackbar? = null
  private var ratingSaveDisposable by singleDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    isCancelable = false
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val root = inflater.inflate(R.layout.layout_linear_vertical, container, false)
    unbinder = ButterKnife.bind(this, root)

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
            viewLifecycleOwner, layoutRoot, rateLink, changeLogIcon, changelog
        )
        .inject(this)

    return root
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    controlsComponent.onUiEvent()
        .subscribe {
          return@subscribe when (it) {
            is VisitMarket -> saveAndRate(it.packageName)
            is Cancel -> saveAndCancel()
          }
        }
        .destroy(viewLifecycleOwner)

    iconComponent.create(savedInstanceState)
    changelogComponent.create(savedInstanceState)
    controlsComponent.create(savedInstanceState)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    iconComponent.saveState(outState)
    changelogComponent.saveState(outState)
    controlsComponent.saveState(outState)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    dismissSnackbar()
    ratingSaveDisposable.tryDispose()
    unbinder.unbind()
  }

  private fun dismissSnackbar() {
    marketErrorSnackbar?.dismiss()
    marketErrorSnackbar = null
  }

  private fun saveAndRate(packageName: String) {
    ratingSaveDisposable = worker.saveRating {
      val error = MarketLinker.linkToMarketPage(requireContext(), packageName)

      // If it errors out we show the view but otherwise we can close
      if (error == null) {
        dismiss()
      } else {
        dismissSnackbar()
        marketErrorSnackbar = Snackbreak.short(
            requireView(),
            "No application is able to handle Store URLs."
        )
            .also { bar -> bar.show() }
      }
    }
  }

  private fun saveAndCancel() {
    ratingSaveDisposable = worker.saveRating {
      dismiss()
    }
  }

  override fun onResume() {
    super.onResume()
    // The dialog is super small for some reason. We have to set the size manually, in onResume
    dialog.window?.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT
    )
  }

  companion object {

    internal const val TAG = "RatingDialog"
    private const val CHANGE_LOG_TEXT = "change_log_text"
    private const val CHANGE_LOG_ICON = "change_log_icon"
    private const val RATE_LINK = "rate_link"

    @CheckResult
    @JvmStatic
    fun newInstance(provider: ChangeLogProvider): RatingDialog {
      return RatingDialog()
          .apply {
            arguments = Bundle().apply {
              putString(RATE_LINK, provider.getPackageName())
              putCharSequence(CHANGE_LOG_TEXT, provider.changelog)
              putInt(CHANGE_LOG_ICON, provider.applicationIcon)
            }
          }
    }
  }
}
