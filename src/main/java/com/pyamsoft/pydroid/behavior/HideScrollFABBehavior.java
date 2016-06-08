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

package com.pyamsoft.pydroid.behavior;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.view.View;

public class HideScrollFABBehavior extends FloatingActionButton.Behavior {

  private final int distanceNeeded;
  private boolean animating = false;

  public HideScrollFABBehavior() {
    this(0);
  }

  public HideScrollFABBehavior(final int distance) {
    super();
    distanceNeeded = distance;
    animating = false;
  }

  public boolean isAnimating() {
    return animating;
  }

  public void endAnimation() {
    this.animating = false;
  }

  @Override
  public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
      View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
    super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed,
        dyUnconsumed);
    if (dyConsumed > distanceNeeded && child.isShown()) {
      if (!animating) {
        animating = true;
        child.hide(new FloatingActionButton.OnVisibilityChangedListener() {
          @Override public void onHidden(FloatingActionButton fab) {
            super.onHidden(fab);
            animating = false;
          }
        });
      }
    } else if (dyConsumed < -distanceNeeded && !child.isShown()) {
      if (!animating) {
        animating = true;
        child.show(new FloatingActionButton.OnVisibilityChangedListener() {
          @Override public void onShown(FloatingActionButton fab) {
            super.onShown(fab);
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
