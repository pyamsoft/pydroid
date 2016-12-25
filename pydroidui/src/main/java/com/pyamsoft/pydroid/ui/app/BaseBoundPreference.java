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
 *
 */

package com.pyamsoft.ui.app;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;

public abstract class BaseBoundPreference extends Preference {

  protected BaseBoundPreference(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  protected BaseBoundPreference(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  protected BaseBoundPreference(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  protected BaseBoundPreference(Context context) {
    super(context);
  }

  @CallSuper @Override public void onBindViewHolder(PreferenceViewHolder holder) {
    super.onBindViewHolder(holder);
    onUnbindViewHolder();
  }

  @CallSuper @Override public void onDetached() {
    super.onDetached();
    onUnbindViewHolder();
  }

  @SuppressWarnings("WeakerAccess") protected void onUnbindViewHolder() {

  }
}
