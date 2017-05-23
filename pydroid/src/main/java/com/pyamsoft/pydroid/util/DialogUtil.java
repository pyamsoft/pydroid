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

package com.pyamsoft.pydroid.util;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.pyamsoft.pydroid.helper.Checker;
import timber.log.Timber;

public final class DialogUtil {

  private DialogUtil() {
    throw new RuntimeException("No instances");
  }

  /**
   * Using the fragment manager to handle transactions, this guarantees that any old
   * versions of the dialog fragment are removed before a new one is added.
   */
  public static void guaranteeSingleDialogFragment(FragmentActivity fragmentActivity,
      @NonNull DialogFragment dialogFragment, @NonNull String tag) {
    if (fragmentActivity == null) {
      Timber.w("Cannot attach a fragment to a NULL activity. No-op");
      return;
    }

    dialogFragment = Checker.checkNonNull(dialogFragment);
    tag = Checker.checkNonNull(tag);

    if (tag.isEmpty()) {
      throw new IllegalArgumentException("Cannot use EMPTY tag");
    }

    final FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
    final FragmentTransaction ft = fragmentManager.beginTransaction();
    final Fragment prev = fragmentManager.findFragmentByTag(tag);
    if (prev != null) {
      Timber.d("Remove existing fragment with tag: %s", tag);
      ft.remove(prev);
    }

    Timber.d("Add new fragment with tag: %s", tag);
    dialogFragment.show(ft, tag);
  }

  /**
   * Guarantees that a fragment with the given tag is only added to the view once
   */
  public static void onlyLoadOnceDialogFragment(FragmentActivity fragmentActivity,
      @NonNull DialogFragment dialogFragment, @NonNull String tag) {
    if (fragmentActivity == null) {
      Timber.w("Cannot attach a fragment to a NULL activity. No-op");
      return;
    }

    dialogFragment = Checker.checkNonNull(dialogFragment);
    tag = Checker.checkNonNull(tag);

    if (tag.isEmpty()) {
      throw new IllegalArgumentException("Cannot use EMPTY tag");
    }

    final FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
    final Fragment prev = fragmentManager.findFragmentByTag(tag);
    if (prev == null) {
      dialogFragment.show(fragmentManager, tag);
    }
  }
}
