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
import android.support.annotation.CallSuper
import android.support.annotation.CheckResult
import android.text.Spannable
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.util.DialogUtil
import com.pyamsoft.pydroid.ui.version.VersionCheckActivity
import com.pyamsoft.pydroid.util.StringUtil
import timber.log.Timber

abstract class RatingActivity : VersionCheckActivity(),
    RatingDialog.ChangeLogProvider,
    RatingPresenter.View {

  internal lateinit var ratingPresenter: RatingPresenter

  override val changeLogText: Spannable
    get() {
      val title = "What's New in Version " + versionName
      val lines = changeLogLines
      val fullLines: Array<String> = Array(lines.size + 1) { "" }
      fullLines[0] = title
      System.arraycopy(lines, 0, fullLines, 1, fullLines.size - 1)
      val spannable = StringUtil.createLineBreakBuilder(fullLines)

      var start = 0
      var end = title.length
      val largeSize = StringUtil.getTextSizeFromAppearance(
          this,
          android.R.attr.textAppearanceLarge
      )
      val largeColor = StringUtil.getTextColorFromAppearance(
          this,
          android.R.attr.textAppearanceLarge
      )
      val smallSize = StringUtil.getTextSizeFromAppearance(
          this,
          android.R.attr.textAppearanceSmall
      )
      val smallColor = StringUtil.getTextColorFromAppearance(
          this,
          android.R.attr.textAppearanceSmall
      )

      StringUtil.boldSpan(spannable, start, end)
      StringUtil.sizeSpan(spannable, start, end, largeSize)
      StringUtil.colorSpan(spannable, start, end, largeColor)

      start += end + 2
      for (line in lines) {
        end += 2 + line.length
      }

      StringUtil.sizeSpan(spannable, start, end, smallSize)
      StringUtil.colorSpan(spannable, start, end, smallColor)

      return spannable
    }

  @get:CheckResult
  protected abstract val changeLogLines: Array<String>

  @get:CheckResult
  protected abstract val versionName: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    PYDroid.obtain()
        .plusRatingComponent(currentApplicationVersion)
        .inject(this)
    ratingPresenter.bind(this, this)
  }

  @CallSuper
  override fun onPostResume() {
    super.onPostResume()

    // Dialog must be shown in onPostResume, or it can crash if device UI performs lifecycle too slowly.
    ratingPresenter.loadRatingDialog(false)
  }

  override fun onShowRatingDialog() {
    DialogUtil.guaranteeSingleDialogFragment(
        this, RatingDialog.newInstance(this),
        RatingDialog.TAG
    )
  }

  override fun onRatingDialogLoadError(throwable: Throwable) {
    Timber.e(throwable, "Could not load rating dialog")
  }
}
