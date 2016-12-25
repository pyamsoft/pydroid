/*
 * Copyright 2016 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.design.fab;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.view.View;
import timber.log.Timber;

/**
 * Floating Action Button behavior which hides button after scroll distance is passed
 */
public class HideScrollFABBehavior extends FloatingActionButton.Behavior {

  private final int distanceNeeded;
  @SuppressWarnings("WeakerAccess") boolean animating = false;

  public HideScrollFABBehavior() {
    this(0);
  }

  public HideScrollFABBehavior(final int distance) {
    super();
    distanceNeeded = distance;
    animating = false;
  }

  @SuppressWarnings("unused") public boolean isAnimating() {
    return animating;
  }

  @SuppressWarnings("unused") public void endAnimation() {
    this.animating = false;
  }

  @SuppressWarnings({ "WeakerAccess", "EmptyMethod" }) public void onHiddenHook() {

  }

  @SuppressWarnings({ "WeakerAccess", "EmptyMethod" }) public void onShownHook() {

  }

  @Override
  public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
      View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
    super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed,
        dyUnconsumed);
    if (dyConsumed > distanceNeeded && child.isShown()) {
      if (!animating) {
        animating = true;
        Timber.w("Hide FAB");
        child.hide(new FloatingActionButton.OnVisibilityChangedListener() {
          @Override public void onHidden(FloatingActionButton fab) {
            super.onHidden(fab);
            onHiddenHook();

            Timber.w(
                "Support library as on 25.1.0 sets FAB visibility to GONE, making it ignore other scrolling event.");
            Timber.w("Set it to invisible to fix this problem");
            fab.setVisibility(View.INVISIBLE);
            animating = false;
          }
        });
      }
    } else if (dyConsumed < -distanceNeeded && !child.isShown()) {
      if (!animating) {
        animating = true;
        Timber.w("Show FAB");
        child.show(new FloatingActionButton.OnVisibilityChangedListener() {
          @Override public void onShown(FloatingActionButton fab) {
            super.onShown(fab);
            onShownHook();
            fab.setVisibility(View.VISIBLE);
            animating = false;
          }
        });
      }
    }
  }

  @Override public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout,
      FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
    return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
  }
}
