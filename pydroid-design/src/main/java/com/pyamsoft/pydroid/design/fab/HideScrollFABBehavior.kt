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

package com.pyamsoft.pydroid.design.fab

import android.support.annotation.CheckResult
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.view.View
import timber.log.Timber

/**
 * Floating Action Button behavior which hides button after scroll distance is passed
 */
class HideScrollFABBehavior(private val distanceNeeded: Int) : FloatingActionButton.Behavior() {

    private var animating = false

    @CheckResult
    fun isAnimating(): Boolean = animating

    constructor() : this(0)

    init {
        animating = false
    }

    fun endAnimation() {
        this.animating = false
    }

    fun onHiddenHook() {

    }

    fun onShownHook() {

    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton,
            target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int,
            type: Int) {
        if (dyConsumed > distanceNeeded && child.isShown) {
            if (!animating) {
                animating = true
                Timber.w("Hide FAB")
                child.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
                    override fun onHidden(fab: FloatingActionButton?) {
                        super.onHidden(fab)
                        onHiddenHook()

                        Timber.w(
                                "Support library as on 25.1.0 sets FAB visibility to GONE, making it ignore other scrolling event.")
                        Timber.w("Set it to invisible to fix this problem")
                        fab?.apply {
                            visibility = View.INVISIBLE
                            animating = false
                        }
                    }
                })
            }
        } else if (dyConsumed < -distanceNeeded && !child.isShown) {
            if (!animating) {
                animating = true
                Timber.w("Show FAB")
                child.show(object : FloatingActionButton.OnVisibilityChangedListener() {
                    override fun onShown(fab: FloatingActionButton?) {
                        super.onShown(fab)
                        onShownHook()
                        fab?.apply {
                            visibility = View.VISIBLE
                            animating = false
                        }
                    }
                })
            }
        }
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout,
            child: FloatingActionButton, directTargetChild: View, target: View, axes: Int,
            type: Int): Boolean = axes == ViewCompat.SCROLL_AXIS_VERTICAL
}
