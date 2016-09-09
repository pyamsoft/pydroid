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

package com.pyamsoft.pydroid.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import com.pyamsoft.pydroid.R;

public class VectorTextView extends NoLeakTextView {

  public VectorTextView(Context context) {
    super(context);
    init(null);
  }

  public VectorTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs);
  }

  public VectorTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs);
  }

  private void init(@Nullable AttributeSet attrs) {
    if (attrs != null) {
      final Context context = getContext();
      final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VectorTextView);

      // Obtain DrawableManager used to pull Drawables safely, and check if we're in RTL
      final boolean rtl = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL;

      // Grab the compat drawable resources from the XML
      final int startDrawableRes = a.getResourceId(R.styleable.VectorTextView_drawableStart, 0);
      final int topDrawableRes = a.getResourceId(R.styleable.VectorTextView_drawableTop, 0);
      final int endDrawableRes = a.getResourceId(R.styleable.VectorTextView_drawableEnd, 0);
      final int bottomDrawableRes = a.getResourceId(R.styleable.VectorTextView_drawableBottom, 0);

      // Load the used drawables, falling back to whatever may be set in an "android:" namespace attribute
      final Drawable[] currentDrawables = getCompoundDrawables();
      final Drawable left =
          startDrawableRes != 0 ? AppCompatResources.getDrawable(context, startDrawableRes)
              : currentDrawables[0];
      final Drawable right =
          endDrawableRes != 0 ? AppCompatResources.getDrawable(context, endDrawableRes)
              : currentDrawables[1];
      final Drawable top =
          topDrawableRes != 0 ? AppCompatResources.getDrawable(context, topDrawableRes)
              : currentDrawables[2];
      final Drawable bottom =
          bottomDrawableRes != 0 ? AppCompatResources.getDrawable(context, bottomDrawableRes)
              : currentDrawables[3];

      // Account for RTL and apply the compound Drawables
      final Drawable start = rtl ? right : left;
      final Drawable end = rtl ? left : right;
      setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom);

      a.recycle();
    }
  }
}
