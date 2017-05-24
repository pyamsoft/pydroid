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
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewCompat
import android.text.Spannable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.LoaderHelper
import com.pyamsoft.pydroid.rating.RatingPresenter
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.fragment.DialogFragmentBase
import com.pyamsoft.pydroid.util.AppUtil
import com.pyamsoft.pydroid.util.DialogUtil
import com.pyamsoft.pydroid.util.NetworkUtil
import kotlinx.android.synthetic.main.dialog_rating.rating_btn_go_rate
import kotlinx.android.synthetic.main.dialog_rating.rating_btn_no_thanks
import kotlinx.android.synthetic.main.dialog_rating.rating_icon
import kotlinx.android.synthetic.main.dialog_rating.rating_text_change
import timber.log.Timber

class RatingDialog : DialogFragmentBase() {
  internal lateinit var rateLink: String
  private var changeLogText: Spannable? = null
  private var versionCode: Int = 0
  @DrawableRes private var changeLogIcon: Int = 0
  private var iconTask = LoaderHelper.empty()

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
  }

  override fun onDestroyView() {
    super.onDestroyView()
    iconTask = LoaderHelper.unload(iconTask)
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater?.inflate(R.layout.dialog_rating, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initDialog()
  }

  private fun initDialog() {
    ViewCompat.setElevation(rating_icon, AppUtil.convertToDP(context, 8f))

    iconTask = LoaderHelper.unload(iconTask)
    iconTask = ImageLoader.fromResource(context, changeLogIcon).into(rating_icon)
    rating_text_change.text = changeLogText

    rating_btn_no_thanks.setOnClickListener({ v ->
      Launcher.INSTANCE.saveVersionCode(versionCode, object : RatingPresenter.SaveCallback {
        override fun onRatingSaved() {
          dismiss()
        }

        override fun onRatingDialogSaveError(throwable: Throwable) {
          Toast.makeText(v.context, "Error occurred while dismissing dialog. May show again later",
              Toast.LENGTH_SHORT).show()
          dismiss()
        }
      })
    })

    rating_btn_go_rate.setOnClickListener({ v ->
      Launcher.INSTANCE.saveVersionCode(versionCode, object : RatingPresenter.SaveCallback {
        override fun onRatingSaved() {
          val fullLink = "market://details?id=" + rateLink
          NetworkUtil.newLink(v.context.applicationContext, fullLink)
          dismiss()
        }

        override fun onRatingDialogSaveError(throwable: Throwable) {
          Toast.makeText(v.context, "Error occurred while dismissing dialog. May show again later",
              Toast.LENGTH_SHORT).show()
          dismiss()
        }
      })
    })
  }

  override fun onDestroy() {
    super.onDestroy()
    Launcher.INSTANCE.cleanup()
  }

  override fun onResume() {
    super.onResume()
    // The dialog is super small for some reason. We have to set the size manually, in onResume
    dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT)
  }

  interface ChangeLogProvider {

    @CheckResult fun getPackageName(): String

    @get:CheckResult val changeLogText: Spannable

    @get:DrawableRes @get:CheckResult val applicationIcon: Int

    @get:CheckResult val currentApplicationVersion: Int
  }

  internal class Launcher private constructor() {

    internal lateinit var presenter: RatingPresenter

    init {
      PYDroid.get().provideComponent().plusRatingComponent().inject(this)
    }

    fun loadRatingDialog(activity: FragmentActivity, provider: ChangeLogProvider, force: Boolean) {
      presenter.loadRatingDialog(provider.currentApplicationVersion, force,
          object : RatingPresenter.RatingCallback {
            override fun onShowRatingDialog() {
              DialogUtil.onlyLoadOnceDialogFragment(activity, newInstance(provider), "rating")
            }

            override fun onRatingDialogLoadError(throwable: Throwable) {
              Timber.e(throwable, "could not load rating dialog")
            }

            override fun onLoadComplete() {
              presenter.destroy()
            }
          })
    }

    fun saveVersionCode(versionCode: Int, callback: RatingPresenter.SaveCallback) {
      presenter.saveRating(versionCode, object : RatingPresenter.SaveCallback {
        override fun onRatingSaved() {
          Timber.d("Saved version code: %d", versionCode)
          callback.onRatingSaved()
        }

        override fun onRatingDialogSaveError(throwable: Throwable) {
          Timber.e(throwable, "error saving version code")
          callback.onRatingDialogSaveError(throwable)
        }
      })
    }

    fun cleanup() {
      presenter.destroy()
    }

    companion object {

      @JvmField internal val INSTANCE = Launcher()
    }
  }

  companion object {

    private val CHANGE_LOG_TEXT = "change_log_text"
    private val CHANGE_LOG_ICON = "change_log_icon"
    private val VERSION_CODE = "version_code"
    private val RATE_LINK = "rate_link"

    @JvmStatic fun showRatingDialog(activity: FragmentActivity, provider: ChangeLogProvider,
        force: Boolean) {
      Launcher.INSTANCE.loadRatingDialog(activity, provider, force)
    }

    @JvmStatic @CheckResult internal fun newInstance(provider: ChangeLogProvider): RatingDialog {
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
