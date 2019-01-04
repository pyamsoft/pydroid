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

import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.text.SpannedString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.annotation.CallSuper
import androidx.annotation.CheckResult
import androidx.core.content.withStyledAttributes
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import com.pyamsoft.pydroid.bootstrap.rating.RatingViewModel
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.tryDispose
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.util.show
import com.pyamsoft.pydroid.ui.version.VersionCheckActivity
import timber.log.Timber

abstract class RatingActivity : VersionCheckActivity(), ChangeLogProvider {

  internal lateinit var ratingViewModel: RatingViewModel

  private var loadRatingDialogDisposable by singleDisposable()
  private var showDialogDisposable by singleDisposable()
  private var showErrorDialogDisposable by singleDisposable()

  protected abstract val changeLogLines: ChangeLogBuilder

  protected abstract val versionName: String

  final override val changelog: SpannedString
    get() {
      return buildSpannedString {
        val attrArray = intArrayOf(android.R.attr.textSize, android.R.attr.textColor).sortedArray()
        val indexOfSize = attrArray.indexOf(android.R.attr.textSize)
        val indexOfColor = attrArray.indexOf(android.R.attr.textColor)
        withStyledAttributes(R.style.TextAppearance_AppCompat_Large, attrArray.copyOf()) {
          val size: Int =
            getDimensionPixelSize(indexOfSize, RESOURCE_NOT_FOUND).validate("dimensionPixelSize")
          val color: Int = getColor(indexOfColor, RESOURCE_NOT_FOUND).validate("color")

          inSpans(StyleSpan(BOLD), AbsoluteSizeSpan(size), ForegroundColorSpan(color)) {
            appendln("What's New in version $versionName")
          }
        }

        withStyledAttributes(R.style.TextAppearance_AppCompat_Small, attrArray.copyOf()) {
          val size: Int =
            getDimensionPixelSize(indexOfSize, RESOURCE_NOT_FOUND).validate("dimensionPixelSize")
          val color: Int = getColor(indexOfColor, RESOURCE_NOT_FOUND).validate("color")

          inSpans(AbsoluteSizeSpan(size), ForegroundColorSpan(color)) {
            for (line in changeLogLines.build()) {
              appendln(line)
            }
          }
        }
      }
    }

  @CheckResult
  private fun Int.validate(what: String): Int {
    if (this == RESOURCE_NOT_FOUND) {
      throw IllegalArgumentException("Value for $what is: $this")
    } else {
      Timber.d("Value for $what is: $this")
      return this
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    PYDroid.obtain(this)
        .inject(this)

    showDialogDisposable = ratingViewModel.onShowRatingDialog { showRatingDialog() }
    showErrorDialogDisposable = ratingViewModel.onShowErrorRatingDialog { error: Throwable ->
      showRatingError(error)
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    loadRatingDialogDisposable.tryDispose()
    showDialogDisposable.tryDispose()
    showErrorDialogDisposable.tryDispose()
  }

  @CallSuper
  override fun onPostResume() {
    super.onPostResume()

    // DialogFragment must be shown in onPostResume, or it can crash if device UI performs lifecycle too slowly.
    loadRatingDialogDisposable = ratingViewModel.loadRatingDialog(
        false,
        onLoadBegin = {},
        onLoadSuccess = { ratingViewModel.publishShowRatingDialog() },
        onLoadError = { error: Throwable -> ratingViewModel.publishShowErrorRatingDialog(error) },
        onLoadComplete = {}
    )
  }

  private fun showRatingDialog() {
    RatingDialog.newInstance(this)
        .show(this, RatingDialog.TAG)
  }

  private fun showRatingError(throwable: Throwable) {
    Timber.e(throwable, "Could not load rating dialog")
  }

  companion object {

    private const val RESOURCE_NOT_FOUND = 0
  }
}
