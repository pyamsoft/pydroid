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

import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.CheckResult
import android.text.SpannedString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.content.withStyledAttributes
import androidx.text.buildSpannedString
import androidx.text.inSpans
import com.pyamsoft.pydroid.base.rating.RatingPresenter
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.util.show
import com.pyamsoft.pydroid.ui.version.VersionCheckActivity
import timber.log.Timber

abstract class RatingActivity : VersionCheckActivity(),
    RatingDialog.ChangeLogProvider,
    RatingPresenter.View {

  internal lateinit var ratingPresenter: RatingPresenter

  override val changeLogText: SpannedString
    get() {
      return buildSpannedString {
        val attrArray = intArrayOf(android.R.attr.textSize, android.R.attr.textColor).sortedArray()
        val indexOfSize = attrArray.indexOf(android.R.attr.textSize)
        val indexOfColor = attrArray.indexOf(android.R.attr.textColor)
        withStyledAttributes(android.R.attr.textAppearanceLarge, attrArray) {
          val size = getDimensionPixelSize(indexOfSize, 0)
          val color = getColor(indexOfColor, 0)

          inSpans(StyleSpan(BOLD), AbsoluteSizeSpan(size), ForegroundColorSpan(color)) {
            append("What's New in version $versionName")
            append("\n")
          }
        }

        withStyledAttributes(android.R.attr.textAppearanceSmall, attrArray) {
          val size = getDimensionPixelSize(indexOfSize, 0)
          val color = getColor(indexOfColor, 0)

          inSpans(AbsoluteSizeSpan(size), ForegroundColorSpan(color)) {
            for (line in changeLogLines) {
              append(line)
              append("\n")
            }
          }
        }

        Timber.d("changeLogText: $this")
      }
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

    // DialogFragment must be shown in onPostResume, or it can crash if device UI performs lifecycle too slowly.
    ratingPresenter.loadRatingDialog(false)
  }

  override fun onShowRating() {
    RatingDialog.newInstance(this)
        .show(this, RatingDialog.TAG)
  }

  override fun onShowRatingError(throwable: Throwable) {
    Timber.e(throwable, "Could not load rating dialog")
  }
}
