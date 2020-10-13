/*
 * Copyright 2020 Peter Kenji Yamanaka
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
import androidx.lifecycle.ViewModelProvider
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.bootstrap.rating.AppReviewLauncher
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.arch.viewModelFactory
import com.pyamsoft.pydroid.ui.rating.RatingControllerEvent.LoadRating
import com.pyamsoft.pydroid.ui.version.VersionCheckActivity
import timber.log.Timber

abstract class RatingActivity : VersionCheckActivity(), ChangeLogProvider {

    private var stateSaver: StateSaver? = null
    internal var ratingFactory: ViewModelProvider.Factory? = null
    private val viewModel by viewModelFactory<RatingViewModel> { ratingFactory }

    @get:CheckResult
    protected abstract val changeLogLines: ChangeLogBuilder

    @get:CheckResult
    protected abstract val versionName: String

    final override val changeLogPackageName: String
        get() = requireNotNull(packageName)

    final override val changelog: SpannedString
        get() = buildSpannedString {
            val attrArray =
                intArrayOf(android.R.attr.textSize, android.R.attr.textColor).sortedArray()
            val indexOfSize = attrArray.indexOf(android.R.attr.textSize)
            val indexOfColor = attrArray.indexOf(android.R.attr.textColor)
            withStyledAttributes(
                R.style.TextAppearance_MaterialComponents_Headline5,
                attrArray.copyOf()
            ) {
                val size: Int =
                    getDimensionPixelSize(
                        indexOfSize,
                        RESOURCE_NOT_FOUND
                    ).validate("dimensionPixelSize")
                val color: Int = getColor(indexOfColor, RESOURCE_NOT_FOUND).validate("color")

                inSpans(StyleSpan(BOLD), AbsoluteSizeSpan(size), ForegroundColorSpan(color)) {
                    appendLine("What's New in version $versionName")
                }
            }

            withStyledAttributes(
                R.style.TextAppearance_MaterialComponents_Body2,
                attrArray.copyOf()
            ) {
                val size: Int =
                    getDimensionPixelSize(
                        indexOfSize,
                        RESOURCE_NOT_FOUND
                    ).validate("dimensionPixelSize")
                val color: Int = getColor(indexOfColor, RESOURCE_NOT_FOUND).validate("color")

                inSpans(AbsoluteSizeSpan(size), ForegroundColorSpan(color)) {
                    changeLogLines.build().forEach { appendLine(it) }
                }
            }
        }

    @CheckResult
    private fun Int.validate(what: String): Int {
        require(this != RESOURCE_NOT_FOUND) { "Value for $what is: $this" }
        Timber.d("Value for $what is: $this")
        return this
    }

    @CallSuper
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Need to do this in onPostCreate because the snackbarRoot will not be available until
        // after subclass onCreate
        Injector.obtain<PYDroidComponent>(applicationContext)
            .plusVersionCheck()
            .create(this) { snackbarRoot }
            .plusRating()
            .create()
            .inject(this)

        stateSaver = createComponent(
            savedInstanceState, this,
            viewModel
        ) {
            return@createComponent when (it) {
                is LoadRating -> showRating(it.launcher)
            }
        }
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        stateSaver?.saveState(outState)
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        ratingFactory = null
        stateSaver = null
    }

    private fun showRating(launcher: AppReviewLauncher) {
        launcher.review(this)
    }

    companion object {

        private const val RESOURCE_NOT_FOUND = 0
    }
}
