/*
 * Copyright 2017 Peter Kenji Yamanaka
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
 */

package com.pyamsoft.pydroid.ui.rating

import android.os.Bundle
import android.support.annotation.CheckResult
import android.support.annotation.DrawableRes
import android.support.v4.view.ViewCompat
import android.text.Spannable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.LoaderHelper
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.app.fragment.DialogFragmentBase
import com.pyamsoft.pydroid.ui.databinding.DialogRatingBinding
import com.pyamsoft.pydroid.ui.helper.Toasty
import com.pyamsoft.pydroid.util.AppUtil
import com.pyamsoft.pydroid.util.NetworkUtil
import com.pyamsoft.pydroid.version.VersionCheckProvider

class RatingDialog : DialogFragmentBase() {
  private lateinit var rateLink: String
  private var versionCode: Int = 0
  private var changeLogText: Spannable? = null
  @DrawableRes private var changeLogIcon: Int = 0
  private var iconTask = LoaderHelper.empty()
  private lateinit var binding: DialogRatingBinding
  internal lateinit var presenter: RatingSavePresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    isCancelable = false

    val launchArguments = arguments
    rateLink = launchArguments.getString(RATE_LINK, null)
    versionCode = launchArguments.getInt(VERSION_CODE, 0)
    changeLogText = launchArguments.getCharSequence(CHANGE_LOG_TEXT, null) as Spannable
    changeLogIcon = launchArguments.getInt(CHANGE_LOG_ICON, 0)

    if (versionCode == 0) {
      throw RuntimeException("Version code cannot be 0")
    }

    if (changeLogText == null) {
      throw RuntimeException("Change Log text cannot be NULL")
    }

    if (changeLogIcon == 0) {
      throw RuntimeException("Change Log Icon Id cannot be 0")
    }

    PYDroid.with {
      it.plusRatingComponent(versionCode).inject(this)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    iconTask = LoaderHelper.unload(iconTask)
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = DialogRatingBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initDialog()

    binding.ratingBtnNoThanks.setOnClickListener {
      presenter.saveRating({ dismiss() }, {
        Toasty.makeText(context.applicationContext,
            "Error occurred while dismissing dialog. May show again later",
            Toasty.LENGTH_SHORT).show()
        dismiss()
      })
    }

    binding.ratingBtnGoRate.setOnClickListener {
      presenter.saveRating({
        val fullLink = "market://details?id=" + rateLink
        NetworkUtil.newLink(it.context.applicationContext, fullLink)
        dismiss()
      }, {
        Toasty.makeText(context.applicationContext,
            "Error occurred while dismissing dialog. May show again later",
            Toasty.LENGTH_SHORT).show()
        dismiss()
      })
    }
  }

  private fun initDialog() {
    ViewCompat.setElevation(binding.ratingIcon, AppUtil.convertToDP(context, 8f))

    iconTask = LoaderHelper.unload(iconTask)
    iconTask = ImageLoader.fromResource(context, changeLogIcon).into(binding.ratingIcon)
    binding.ratingTextChange.text = changeLogText
  }

  override fun onStart() {
    super.onStart()
    presenter.start(Unit)
  }

  override fun onStop() {
    super.onStop()
    presenter.stop()
  }

  override fun onResume() {
    super.onResume()
    // The dialog is super small for some reason. We have to set the size manually, in onResume
    dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT)
  }

  interface ChangeLogProvider : VersionCheckProvider {

    @CheckResult fun getPackageName(): String

    @get:CheckResult val changeLogText: Spannable

    @get:DrawableRes @get:CheckResult val applicationIcon: Int
  }

  companion object {

    private const val CHANGE_LOG_TEXT = "change_log_text"
    private const val CHANGE_LOG_ICON = "change_log_icon"
    private const val VERSION_CODE = "version_code"
    private const val RATE_LINK = "rate_link"

    @JvmStatic @CheckResult fun newInstance(provider: ChangeLogProvider): RatingDialog {
      val fragment = RatingDialog()
      val args = Bundle()
      args.putString(RATE_LINK, provider.getPackageName())
      args.putCharSequence(CHANGE_LOG_TEXT, provider.changeLogText)
      args.putInt(VERSION_CODE, provider.currentApplicationVersion)
      args.putInt(CHANGE_LOG_ICON, provider.applicationIcon)
      fragment.arguments = args
      return fragment
    }
  }
}
