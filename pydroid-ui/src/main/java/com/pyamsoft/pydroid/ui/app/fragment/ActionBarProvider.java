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

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

interface ActionBarProvider {

  void setActionBarUpEnabled(boolean up);

  @SuppressWarnings("unused") void setActionBarUpEnabled(boolean up, @DrawableRes int icon);

  @SuppressWarnings("unused") void setActionBarUpEnabled(boolean up, @Nullable Drawable icon);

  void setActionBarTitle(@NonNull CharSequence title);

  void setActionBarTitle(@StringRes int title);
}
