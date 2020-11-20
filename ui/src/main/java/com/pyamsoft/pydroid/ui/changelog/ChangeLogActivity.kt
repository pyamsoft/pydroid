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

package com.pyamsoft.pydroid.ui.changelog

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
import com.google.android.material.R
import com.pyamsoft.pydroid.arch.StateSaver
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.arch.viewModelFactory
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogViewModel
import com.pyamsoft.pydroid.ui.rating.RatingActivity

abstract class ChangeLogActivity : RatingActivity(), ChangeLogProvider {

    private var stateSaver: StateSaver? = null

    internal var changeLogFactory: ViewModelProvider.Factory? = null
    private val viewModel by viewModelFactory<ChangeLogViewModel> { changeLogFactory }

    protected abstract val changeLogLines: ChangeLogBuilder

    protected abstract val versionName: String

    final override val changeLogPackageName: String
        get() = requireNotNull(packageName)

    final override val changelog: SpannedString
        get() = buildSpannedString {
            val attrArray = intArrayOf(android.R.attr.textSize, android.R.attr.textColor)

            val indexOfSize = 0
            val indexOfColor = 1

            withStyledAttributes(R.style.TextAppearance_MaterialComponents_Headline5, attrArray) {
                val size =
                    getDimensionPixelSize(indexOfSize, INVALID).validate("dimensionPixelSize")
                val color: Int = getColor(indexOfColor, INVALID).validate("color")

                inSpans(StyleSpan(BOLD), AbsoluteSizeSpan(size), ForegroundColorSpan(color)) {
                    appendLine("What's New in version $versionName")
                }
            }

            withStyledAttributes(R.style.TextAppearance_MaterialComponents_Body2, attrArray) {
                val size: Int =
                    getDimensionPixelSize(indexOfSize, INVALID).validate("dimensionPixelSize")
                val color: Int = getColor(indexOfColor, INVALID).validate("color")

                inSpans(AbsoluteSizeSpan(size), ForegroundColorSpan(color)) {
                    changeLogLines.build().forEach {
                        // TODO(Peter): New style
                        appendLine(it.line)
                    }
                }
            }
        }

    @CheckResult
    private fun Int.validate(what: String): Int {
        return this.also { require(it != INVALID) { "Value for $what is: $it" } }
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Injector.obtain<PYDroidComponent>(applicationContext)
            .plusChangeLog()
            .create()
            .inject(this)

        stateSaver = createComponent(
            savedInstanceState, this,
            viewModel
        ) {
            // TODO(Peter)
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
        changeLogFactory = null
        stateSaver = null
    }

    companion object {

        private const val INVALID = 0

    }
}
