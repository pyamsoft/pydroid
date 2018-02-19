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
import android.support.annotation.CheckResult
import android.support.annotation.DrawableRes
import android.support.v4.view.ViewCompat
import android.text.SpannedString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.pyamsoft.pydroid.base.rating.RatingSavePresenter
import com.pyamsoft.pydroid.base.version.VersionCheckProvider
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarDialog
import com.pyamsoft.pydroid.ui.databinding.DialogRatingBinding
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener
import com.pyamsoft.pydroid.util.Toasty
import com.pyamsoft.pydroid.util.hyperlink
import com.pyamsoft.pydroid.util.toDp
import timber.log.Timber

internal class RatingDialog : ToolbarDialog(), RatingSavePresenter.View {

  internal lateinit var imageLoader: ImageLoader
  internal lateinit var presenter: RatingSavePresenter
  private lateinit var rateLink: String
  private var versionCode: Int = 0
  private var changeLogText: SpannedString? = null
  @DrawableRes
  private var changeLogIcon: Int = 0
  private lateinit var binding: DialogRatingBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    isCancelable = false

    arguments?.let {
      rateLink = it.getString(RATE_LINK, null)
      versionCode = it.getInt(VERSION_CODE, 0)
      changeLogText = it.getCharSequence(CHANGE_LOG_TEXT, null) as SpannedString
      changeLogIcon = it.getInt(CHANGE_LOG_ICON, 0)
    }

    if (versionCode == 0) {
      throw RuntimeException("Version code cannot be 0")
    }

    if (changeLogIcon == 0) {
      throw RuntimeException("Change Log Icon Id cannot be 0")
    }

    PYDroid.obtain()
        .plusRatingComponent(versionCode)
        .inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DialogRatingBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    initDialog()

    binding.apply {
      ratingBtnNoThanks.setOnDebouncedClickListener { presenter.saveRating(false) }
      ratingBtnGoRate.setOnDebouncedClickListener { presenter.saveRating(true) }
    }

    presenter.bind(viewLifecycle, this)
  }

  override fun onRatingSaved(accept: Boolean) {
    if (accept) {
      val fullLink = "market://details?id=" + rateLink
      fullLink.hyperlink(context!!)
          .navigate()
    }

    dismiss()
  }

  override fun onRatingSaveError(throwable: Throwable) {
    Toasty.makeText(
        context!!.applicationContext,
        "Error occurred while dismissing dialog. May show again later",
        Toasty.LENGTH_SHORT
    )
    dismiss()
  }

  private fun initDialog() {
    ViewCompat.setElevation(
        binding.ratingIcon,
        8.toDp(binding.ratingIcon.context).toFloat()
    )

    imageLoader.fromResource(changeLogIcon)
        .into(binding.ratingIcon)
        .bind(viewLifecycle)

    Timber.d("changeLogText: $changeLogText")
    binding.ratingTextChange.text = changeLogText
  }

  override fun onResume() {
    super.onResume()
    // The dialog is super small for some reason. We have to set the size manually, in onResume
    dialog.window?.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT
    )
  }

  interface ChangeLogProvider : VersionCheckProvider {

    @CheckResult
    fun getPackageName(): String

    @get:CheckResult
    val changeLogText: SpannedString

    @get:DrawableRes
    @get:CheckResult
    val applicationIcon: Int
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
          putCharSequence(CHANGE_LOG_TEXT, provider.changeLogText)
          putInt(VERSION_CODE, provider.currentApplicationVersion)
          putInt(CHANGE_LOG_ICON, provider.applicationIcon)
        }
      }
    }
  }
}
