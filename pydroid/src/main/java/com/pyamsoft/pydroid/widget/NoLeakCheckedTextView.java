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

package com.pyamsoft.pydroid.widget;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.util.AttributeSet;

/**
 * Attempts to fix TextView memory leak
 *
 * https://github.com/square/leakcanary/issues/180
 */
public class NoLeakCheckedTextView extends AppCompatCheckedTextView {

  public NoLeakCheckedTextView(Context context) {
    super(context);
  }

  public NoLeakCheckedTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public NoLeakCheckedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @CallSuper @Override protected void onDetachedFromWindow() {
    getViewTreeObserver().removeOnPreDrawListener(this);
    super.onDetachedFromWindow();
  }
}
