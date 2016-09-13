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

package com.pyamsoft.pydroid.base;

import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import com.pyamsoft.pydroid.lib.PYDroidApplication;

public abstract class ActionBarFragment extends Fragment implements ActionBarProvider {

  @Override @CheckResult @Nullable public ActionBar getActionBar() {
    final FragmentActivity activity = getActivity();
    if (activity instanceof AppCompatActivity) {
      final AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
      return appCompatActivity.getSupportActionBar();
    } else {
      throw new ClassCastException("Activity not instance of AppCompatActivity");
    }
  }

  @Override public void setActionBarUpEnabled(boolean up) {
    final ActionBar bar = getActionBar();
    if (bar != null) {
      bar.setHomeButtonEnabled(up);
      bar.setDisplayHomeAsUpEnabled(up);
    }
  }

  @CallSuper @Override public void onDestroy() {
    super.onDestroy();
    PYDroidApplication.getRefWatcher(this).watch(this);
  }
}
