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
import android.text.Spannable
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.util.DialogUtil
import com.pyamsoft.pydroid.ui.version.VersionCheckActivity
import com.pyamsoft.pydroid.util.StringUtil
import timber.log.Timber

abstract class RatingActivity : VersionCheckActivity(), RatingDialog.ChangeLogProvider {

  internal lateinit var ratingPresenter: RatingPresenter

  override val changeLogText: Spannable
    get() {
      val title = "What's New in Version " + versionName
      val lines = changeLogLines
      val fullLines = Array(lines.size + 1, { "" })
      fullLines[0] = title
      System.arraycopy(lines, 0, fullLines, 1, fullLines.size - 1)
      val spannable = StringUtil.createLineBreakBuilder(*fullLines)

      var start = 0
      var end = title.length
      val largeSize = StringUtil.getTextSizeFromAppearance(this, android.R.attr.textAppearanceLarge)
      val largeColor = StringUtil.getTextColorFromAppearance(this,
          android.R.attr.textAppearanceLarge)
      val smallSize = StringUtil.getTextSizeFromAppearance(this, android.R.attr.textAppearanceSmall)
      val smallColor = StringUtil.getTextColorFromAppearance(this,
          android.R.attr.textAppearanceSmall)

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

  @get:CheckResult protected abstract val changeLogLines: Array<String>

  @get:CheckResult protected abstract val versionName: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    PYDroid.with {
      it.plusRatingComponent(currentApplicationVersion).inject(this)
    }
  }

  override fun onStart() {
    super.onStart()
    ratingPresenter.start(Unit)
  }

  override fun onStop() {
    super.onStop()
    ratingPresenter.stop()
  }

  override fun onPostResume() {
    super.onPostResume()

    // Dialog must be shown in onPostResume, or it can crash if device UI performs lifecycle too slowly.
    val activity = this
    ratingPresenter.loadRatingDialog(false, onShowRatingDialog = {
      DialogUtil.guaranteeSingleDialogFragment(activity, RatingDialog.newInstance(activity),
          "rating")
    }, onRatingDialogLoadError = {
      Timber.e(it, "Could not load rating dialog")
    })
  }
}
