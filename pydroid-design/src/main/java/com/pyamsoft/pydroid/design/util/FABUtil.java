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

package com.pyamsoft.pydroid.design.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.ViewGroup;
import com.pyamsoft.pydroid.helper.Checker;

public final class FABUtil {

  private FABUtil() {
    throw new RuntimeException("No instances");
  }

  public static void setupFABBehavior(@NonNull FloatingActionButton fab,
      final @Nullable FloatingActionButton.Behavior behavior) {
    fab = Checker.checkNonNull(fab);

    final ViewGroup.LayoutParams params = fab.getLayoutParams();
    if (params instanceof CoordinatorLayout.LayoutParams) {
      final CoordinatorLayout.LayoutParams coordParams = (CoordinatorLayout.LayoutParams) params;
      if (behavior == null) {
        coordParams.setBehavior(new FloatingActionButton.Behavior());
      } else {
        coordParams.setBehavior(behavior);
      }
    }
  }
}
