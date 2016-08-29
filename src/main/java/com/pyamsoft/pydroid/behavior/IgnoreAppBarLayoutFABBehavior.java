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

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import java.util.List;

public class IgnoreAppBarLayoutFABBehavior extends FloatingActionButton.Behavior {

  private boolean control;

  public IgnoreAppBarLayoutFABBehavior() {
    this(true);
  }

  public IgnoreAppBarLayoutFABBehavior(final boolean b) {
    super();
    control = b;
  }

  public final void setControl(final boolean b) {
    control = b;
  }

  @Override
  public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child,
      View dependency) {
    if (dependency instanceof AppBarLayout) {
      // If the adapter does not want to show the fab button, return false
      if (!control) {
        return false;
      }
    }
    return super.onDependentViewChanged(parent, child, dependency);
  }

  @Override public boolean onLayoutChild(CoordinatorLayout parent, FloatingActionButton child,
      int layoutDirection) {
    final List<View> dependencies = parent.getDependencies(child);
    for (int i = 0, count = dependencies.size(); i < count; i++) {
      final View dependency = dependencies.get(i);
      if (dependency instanceof AppBarLayout) {
        if (!control) {
          return false;
        }
        break;
      }
    }
    return super.onLayoutChild(parent, child, layoutDirection);
  }
}
