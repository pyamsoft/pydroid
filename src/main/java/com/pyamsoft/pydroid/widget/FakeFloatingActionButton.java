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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.CheckResult;
import android.support.annotation.Size;
import android.support.v4.content.res.ConfigurationHelper;
import android.util.AttributeSet;
import android.widget.ImageButton;
import com.pyamsoft.pydroid.util.AppUtil;
import timber.log.Timber;

/**
 * A fake floating action button
 */
public class FakeFloatingActionButton extends ImageButton {

  /**
   * The switch point for the largest screen edge where SIZE_AUTO switches from mini to normal.
   */
  private static final int AUTO_MINI_LARGEST_SCREEN_WIDTH = 470;
  private int fabMiniSize;
  private int fabNormalSize;

  public FakeFloatingActionButton(Context context) {
    super(context);
    init();
  }

  public FakeFloatingActionButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public FakeFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  public FakeFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    fabMiniSize = (int) AppUtil.convertToDP(getContext(), 40);
    fabNormalSize = (int) AppUtil.convertToDP(getContext(), 56);

    // Always set scale type to center
    setScaleType(ScaleType.CENTER);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int preferredSize = getSizeDimension();

    final int w = resolveAdjustedSize(preferredSize, widthMeasureSpec);
    final int h = resolveAdjustedSize(preferredSize, heightMeasureSpec);

    // As we want to stay circular, we set both dimensions to be the
    // smallest resolved dimension
    final int d = Math.min(w, h);
    Timber.d("Set FakeFloatingActionButton size to %ddp", d);
    setMeasuredDimension(d, d);
  }

  @SuppressLint("SwitchIntDef") @CheckResult @Size
  private int resolveAdjustedSize(int desiredSize, int measureSpec) {
    final int result;
    final int specMode = MeasureSpec.getMode(measureSpec);
    final int specSize = MeasureSpec.getSize(measureSpec);
    switch (specMode) {
      case MeasureSpec.AT_MOST:
        // Parent says we can be as big as we want, up to specSize.
        // Don't be larger than specSize, and don't be larger than
        // the max size imposed on ourselves.
        result = Math.min(desiredSize, specSize);
        break;
      case MeasureSpec.EXACTLY:
        // No choice. Do what we are told.
        result = specSize;
        break;
      default:
        // Parent says we can be as big as we want. Just don't be larger
        // than max size imposed on ourselves.
        result = desiredSize;
    }

    return result;
  }

  @CheckResult private int getSizeDimension() {
    // Need the View resources, or else it gives us back the full window size
    final Resources res = getResources();
    final int screenWidth = ConfigurationHelper.getScreenWidthDp(res);
    final int screenHeight = ConfigurationHelper.getScreenHeightDp(res);
    Timber.d("Screen width: %d", screenWidth);
    Timber.d("Screen height: %d", screenHeight);

    // If we're set to auto, grab the size from resources and refresh
    final boolean belowMinScreenSize =
        Math.max(screenWidth, screenHeight) < AUTO_MINI_LARGEST_SCREEN_WIDTH;
    Timber.d("Below min screen size: %s", belowMinScreenSize);

    return belowMinScreenSize ? fabMiniSize : fabNormalSize;
  }
}
