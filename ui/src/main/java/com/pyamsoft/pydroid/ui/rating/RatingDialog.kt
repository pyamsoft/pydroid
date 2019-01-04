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

package com.pyamsoft.pydroid.ui.rating

import android.os.Bundle
import android.text.SpannedString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.CheckResult
import androidx.annotation.DrawableRes
import com.pyamsoft.pydroid.bootstrap.rating.RatingViewModel
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.tryDispose
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarDialog
import com.pyamsoft.pydroid.ui.app.fragment.requireArguments
import com.pyamsoft.pydroid.ui.app.fragment.requireView
import com.pyamsoft.pydroid.ui.util.MarketLinker

internal class RatingDialog : ToolbarDialog() {

  @DrawableRes private var changeLogIcon: Int = 0
  private lateinit var rateLink: String
  private lateinit var changelog: SpannedString

  internal lateinit var imageLoader: ImageLoader
  internal lateinit var rootView: RatingDialogView
  internal lateinit var viewModel: RatingViewModel

  private var ratingSaveDisposable by singleDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    isCancelable = false

    requireArguments().also {
      rateLink = it.getString(RATE_LINK, null)
      changeLogIcon = it.getInt(CHANGE_LOG_ICON, 0)
      changelog = requireNotNull(it.getCharSequence(CHANGE_LOG_TEXT, null)) as SpannedString
    }

    if (changeLogIcon == 0) {
      throw RuntimeException("Change Log Icon Id cannot be 0")
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    PYDroid.obtain(requireContext())
        .plusRatingDialogComponent(
            viewLifecycleOwner, inflater, container, changeLogIcon, changelog
        )
        .inject(this)

    rootView.create()
    return rootView.root()
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    rootView.onSaveRating { saveRating(true) }
    rootView.onCancelRating { saveRating(false) }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    ratingSaveDisposable.tryDispose()
  }

  private fun saveRating(visitMarket: Boolean) {
    ratingSaveDisposable = viewModel.saveRating(
        onSaveSuccess = {
          if (visitMarket) {
            onRatingSaved()
          }
        },
        onSaveError = { error: Throwable -> onRatingSaveError(error) },
        onSaveComplete = { dismiss() }
    )
  }

  private fun onRatingSaved() {
    requireView().also { MarketLinker.linkToMarketPage(it.context.packageName, it) }
  }

  private fun onRatingSaveError(error: Throwable) {
    viewModel.publishSaveRatingError(error)
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
