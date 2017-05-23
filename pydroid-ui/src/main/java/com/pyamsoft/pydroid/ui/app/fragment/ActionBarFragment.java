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

package com.pyamsoft.pydroid.ui.app.fragment;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import com.pyamsoft.pydroid.ui.app.activity.ActivityBase;
import com.pyamsoft.pydroid.ui.util.ActionBarUtil;

public abstract class ActionBarFragment extends Fragment implements ActionBarProvider {

  @Override public void setActionBarUpEnabled(boolean up) {
    ActionBarUtil.setActionBarUpEnabled(getActivity(), up);
  }

  @Override public void setActionBarUpEnabled(boolean up, @DrawableRes int icon) {
    ActionBarUtil.setActionBarUpEnabled(getActivity(), up, icon);
  }

  @Override public void setActionBarUpEnabled(boolean up, @Nullable Drawable icon) {
    ActionBarUtil.setActionBarUpEnabled(getActivity(), up, icon);
  }

  @Override public void setActionBarTitle(@NonNull CharSequence title) {
    ActionBarUtil.setActionBarTitle(getActivity(), title);
  }

  @Override public void setActionBarTitle(@StringRes int title) {
    ActionBarUtil.setActionBarTitle(getActivity(), title);
  }
}
