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

package com.pyamsoft.pydroid.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

  @SuppressWarnings("WeakerAccess") public static final int HORIZONTAL_LIST =
      LinearLayoutManager.HORIZONTAL;
  public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
  @NonNull private static final int[] ATTRS = new int[] { android.R.attr.listDivider };
  @NonNull private final Drawable dividerDrawable;
  private int decorationOrientation;

  public DividerItemDecoration(@NonNull Context context, int orientation) {
    final TypedArray a = context.obtainStyledAttributes(ATTRS);
    final Drawable drawable = a.getDrawable(0);
    assert drawable != null;
    dividerDrawable = drawable;
    a.recycle();
    setOrientation(orientation);
  }

  @SuppressWarnings("WeakerAccess") public void setOrientation(int orientation) {
    if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
      throw new IllegalArgumentException("invalid orientation");
    }
    decorationOrientation = orientation;
  }

  @Override public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent,
      @NonNull RecyclerView.State state) {
    if (decorationOrientation == VERTICAL_LIST) {
      drawVertical(c, parent);
    } else {
      drawHorizontal(c, parent);
    }
  }

  private void draw(@NonNull Canvas c, final int left, final int top, final int right,
      final int bottom) {
    dividerDrawable.setBounds(left, top, right, bottom);
    dividerDrawable.draw(c);
  }

  private void drawVertical(@NonNull Canvas c, @NonNull RecyclerView parent) {
    final int left = parent.getPaddingLeft();
    final int right = parent.getWidth() - parent.getPaddingRight();
    final int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
      final int top = child.getBottom() + params.bottomMargin;
      final int bottom = top + dividerDrawable.getIntrinsicHeight();
      draw(c, left, top, right, bottom);
    }
  }

  private void drawHorizontal(@NonNull Canvas c, @NonNull RecyclerView parent) {
    final int top = parent.getPaddingTop();
    final int bottom = parent.getHeight() - parent.getPaddingBottom();
    final int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
      final int left = child.getRight() + params.rightMargin;
      final int right = left + dividerDrawable.getIntrinsicHeight();
      draw(c, left, top, right, bottom);
    }
  }

  @Override public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
      @NonNull RecyclerView parent, RecyclerView.State state) {
    if (decorationOrientation == VERTICAL_LIST) {
      outRect.set(0, 0, 0, dividerDrawable.getIntrinsicHeight());
    } else {
      outRect.set(0, 0, dividerDrawable.getIntrinsicWidth(), 0);
    }
  }
}

