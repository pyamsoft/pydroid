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
import androidx.annotation.CheckResult
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.tryDispose
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarDialog
import com.pyamsoft.pydroid.ui.app.fragment.requireArguments
import com.pyamsoft.pydroid.ui.app.fragment.requireView
import com.pyamsoft.pydroid.ui.arch.destroy
import com.pyamsoft.pydroid.ui.databinding.LayoutConstraintBinding
import com.pyamsoft.pydroid.ui.rating.ChangeLogProvider
import com.pyamsoft.pydroid.ui.rating.dialog.RatingViewEvent.Cancel
import com.pyamsoft.pydroid.ui.rating.dialog.RatingViewEvent.VisitMarket
import com.pyamsoft.pydroid.ui.util.MarketLinker

internal class RatingDialog : ToolbarDialog() {

  private lateinit var binding: LayoutConstraintBinding

  internal lateinit var iconComponent: RatingIconUiComponent
  internal lateinit var changelogComponent: RatingChangelogUiComponent
  internal lateinit var controlsComponent: RatingControlsUiComponent
  internal lateinit var worker: RatingDialogWorker

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
    binding = LayoutConstraintBinding.inflate(inflater, container, false)

    val rateLink = requireArguments().getString(RATE_LINK, "")
    val changeLogIcon = requireArguments().getInt(CHANGE_LOG_ICON, 0)
    val changelog = requireArguments().getCharSequence(CHANGE_LOG_TEXT, "") as? SpannedString

    requireNotNull(rateLink)
    requireNotNull(changelog)

    require(rateLink.isNotBlank())
    require(changeLogIcon > 0)
    require(changelog.isNotBlank())


    PYDroid.obtain(binding.root.context.applicationContext)
        .plusRatingDialogComponent(
            binding.layoutRoot, viewLifecycleOwner, rateLink, changeLogIcon, changelog
        )
        .inject(this)

    return binding.root
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    controlsComponent.onUiEvent()
        .subscribe {
          when (it) {
            is VisitMarket -> saveRating(it.packageName)
            is Cancel -> saveRating()
          }
        }
        .destroy(viewLifecycleOwner)

    iconComponent.create(savedInstanceState)
    changelogComponent.create(savedInstanceState)
    controlsComponent.create(savedInstanceState)

    applyConstraints(binding.layoutRoot)
  }

  private fun applyConstraints(layoutRoot: ConstraintLayout) {
    ConstraintSet().apply {
      clone(layoutRoot)

      iconComponent.also {
        connect(it.id(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        connect(it.id(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        connect(it.id(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        constrainHeight(it.id(), ConstraintSet.WRAP_CONTENT)
        constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
      }

      controlsComponent.also {
        connect(it.id(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        connect(it.id(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        connect(it.id(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        constrainHeight(it.id(), ConstraintSet.WRAP_CONTENT)
        constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
      }

      changelogComponent.also {
        connect(it.id(), ConstraintSet.TOP, iconComponent.id(), ConstraintSet.BOTTOM)
        connect(it.id(), ConstraintSet.BOTTOM, controlsComponent.id(), ConstraintSet.TOP)
        connect(it.id(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        connect(it.id(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        constrainHeight(it.id(), ConstraintSet.MATCH_CONSTRAINT)
        constrainWidth(it.id(), ConstraintSet.MATCH_CONSTRAINT)
      }

      applyTo(layoutRoot)
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    iconComponent.saveState(outState)
    changelogComponent.saveState(outState)
    controlsComponent.saveState(outState)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    ratingSaveDisposable.tryDispose()
  }

  private fun saveRating(packageName: String = "") {
    ratingSaveDisposable = worker.saveRating {
      if (packageName.isNotBlank()) {
        MarketLinker.linkToMarketPage(packageName, requireView())
      }
    }
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
