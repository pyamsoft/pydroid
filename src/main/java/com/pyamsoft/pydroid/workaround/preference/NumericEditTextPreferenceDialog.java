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

package com.pyamsoft.pydroid.workaround.preference;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.EditTextPreferenceDialogFragmentCompat;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

public final class NumericEditTextPreferenceDialog extends EditTextPreferenceDialogFragmentCompat {

  @NonNull public static final String DIALOG_FRAGMENT_TAG =
      "android.support.v7.preference.PreferenceFragment.DIALOG";

  @Nullable private EditText editText;

  @CheckResult @NonNull public static NumericEditTextPreferenceDialog newInstance(String key) {
    final NumericEditTextPreferenceDialog fragment = new NumericEditTextPreferenceDialog();
    final Bundle b = new Bundle(1);
    b.putString(ARG_KEY, key);
    fragment.setArguments(b);
    return fragment;
  }

  @Override protected void onBindDialogView(View view) {
    super.onBindDialogView(view);
    editText = (EditText) view.findViewById(android.R.id.edit);
    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
  }
}
