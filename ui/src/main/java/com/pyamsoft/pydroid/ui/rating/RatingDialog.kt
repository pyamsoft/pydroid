/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import androidx.core.view.ViewCompat
import com.pyamsoft.pydroid.bootstrap.rating.RatingViewModel
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarDialog
import com.pyamsoft.pydroid.ui.app.fragment.requireArguments
import com.pyamsoft.pydroid.ui.databinding.DialogRatingBinding
import com.pyamsoft.pydroid.ui.util.MarketLinker
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener
import com.pyamsoft.pydroid.util.toDp

internal class RatingDialog : ToolbarDialog() {

  private lateinit var rateLink: String
  private lateinit var binding: DialogRatingBinding
  internal lateinit var imageLoader: ImageLoader
  internal lateinit var viewModel: RatingViewModel
  internal lateinit var errorPublisher: Publisher<Throwable>
  @DrawableRes private var changeLogIcon: Int = 0
  private var versionCode: Int = 0
  private var changelog: SpannedString? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    isCancelable = false
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    requireArguments().also {
      rateLink = it.getString(RATE_LINK, null)
      versionCode = it.getInt(VERSION_CODE, 0)
      changelog = it.getCharSequence(CHANGE_LOG_TEXT, null) as SpannedString
      changeLogIcon = it.getInt(CHANGE_LOG_ICON, 0)
    }

    if (versionCode == 0) {
      throw RuntimeException("Version code cannot be 0")
    }

    if (changeLogIcon == 0) {
      throw RuntimeException("Change Log Icon Id cannot be 0")
    }

    PYDroid.obtain(requireContext())
        .plusRatingComponent(viewLifecycleOwner, versionCode)
        .inject(this)

    binding = DialogRatingBinding.inflate(inflater, container, false)

    initDialog()
    observeRatingSaved()

    return binding.root
  }

  private fun observeRatingSaved() {
    viewModel.onRatingSaved { wrapper ->
      wrapper.onSuccess { onRatingSaved(it) }
      wrapper.onError { onRatingSaveError(it) }
      wrapper.onComplete { dismiss() }
    }
  }

  private fun onRatingSaved(accept: Boolean) {
    if (accept) {
      view?.also { MarketLinker.linkToMarketPage(it.context.packageName, it) }
    }
  }

  private fun onRatingSaveError(throwable: Throwable) {
    errorPublisher.publish(throwable)
  }

  private fun initDialog() {
    ViewCompat.setElevation(
        binding.ratingIcon,
        8.toDp(binding.ratingIcon.context).toFloat()
    )

    imageLoader.load(changeLogIcon)
        .into(binding.ratingIcon)
        .bind(viewLifecycleOwner)
    binding.ratingTextChange.text = changelog

    binding.apply {
      ratingBtnNoThanks.setOnDebouncedClickListener {
        viewModel.saveRating(false)
      }
      ratingBtnGoRate.setOnDebouncedClickListener {
        viewModel.saveRating(true)
      }
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
    private const val VERSION_CODE = "version_code"
    private const val RATE_LINK = "rate_link"

    @CheckResult
    @JvmStatic
    fun newInstance(provider: ChangeLogProvider): RatingDialog {
      return RatingDialog().apply {
        arguments = Bundle().apply {
          putString(RATE_LINK, provider.getPackageName())
          putCharSequence(CHANGE_LOG_TEXT, provider.changelog)
          putInt(VERSION_CODE, provider.currentApplicationVersion)
          putInt(CHANGE_LOG_ICON, provider.applicationIcon)
        }
      }
    }
  }
}
