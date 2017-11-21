/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.pyamsoft.pydroid.ui.rating

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.CheckResult
import android.text.Spannable
import com.pyamsoft.pydroid.presenter.Presenter
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.util.DialogUtil
import com.pyamsoft.pydroid.ui.version.VersionCheckActivity
import com.pyamsoft.pydroid.util.StringUtil
import timber.log.Timber

abstract class RatingActivity : VersionCheckActivity(), RatingDialog.ChangeLogProvider,
        RatingPresenter.View {

    internal lateinit var ratingPresenter: RatingPresenter

    @CallSuper override fun provideBoundPresenters(): List<Presenter<*>> =
            listOf(ratingPresenter) + super.provideBoundPresenters()

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
            val largeSize = StringUtil.getTextSizeFromAppearance(this,
                    android.R.attr.textAppearanceLarge)
            val largeColor = StringUtil.getTextColorFromAppearance(this,
                    android.R.attr.textAppearanceLarge)
            val smallSize = StringUtil.getTextSizeFromAppearance(this,
                    android.R.attr.textAppearanceSmall)
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
        PYDroid.obtain().plusRatingComponent(currentApplicationVersion).inject(this)
        ratingPresenter.bind(this)
    }

    @CallSuper
    override fun onPostResume() {
        super.onPostResume()

        // Dialog must be shown in onPostResume, or it can crash if device UI performs lifecycle too slowly.
        ratingPresenter.loadRatingDialog(false)
    }

    override fun onShowRatingDialog() {
        DialogUtil.guaranteeSingleDialogFragment(this, RatingDialog.newInstance(this),
                RatingDialog.TAG)
    }

    override fun onRatingDialogLoadError(throwable: Throwable) {
        Timber.e(throwable, "Could not load rating dialog")
    }
}
