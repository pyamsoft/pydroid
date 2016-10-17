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
import android.graphics.drawable.Drawable;
import android.support.annotation.CheckResult;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.OverScroller;
import timber.log.Timber;

public class ScrollingImageView extends ImageView {

  @NonNull final FinalizedScrollComponents scrollComponents = new FinalizedScrollComponents();
  int positionX = 0;
  int positionY = 0;
  // We immediately initialize, so this is NonNull guaranteed
  @Dimension private int screenW;
  @Dimension private int screenH;

  public ScrollingImageView(Context context) {
    super(context);
    init(context);
  }

  public ScrollingImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public ScrollingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  public ScrollingImageView(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context);
  }

  private void init(@NonNull Context context) {
    // We will need screen dimensions to make sure we don't over scroll the
    // image
    final DisplayMetrics dm = getResources().getDisplayMetrics();
    screenW = dm.widthPixels;
    screenH = dm.heightPixels;

    final GestureDetector.OnGestureListener gestureListener = new ScrollingGestureListener(this);
    scrollComponents.setGestureDetector(new GestureDetectorCompat(context, gestureListener));
    scrollComponents.setOverScroller(new OverScroller(context));
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    scrollComponents.getGestureDetector().onTouchEvent(event);
    return true;
  }

  @Override public void computeScroll() {
    super.computeScroll();
    // computeScrollOffset() returns true only when the scrolling isn't
    // already finished
    final OverScroller overScroller = scrollComponents.getOverScroller();
    if (overScroller.computeScrollOffset()) {
      positionX = overScroller.getCurrX();
      positionY = overScroller.getCurrY();
      scrollTo(positionX, positionY);
    } else {
      // when scrolling is over, we will want to "spring back" if the
      // image is overscrolled
      overScroller.springBack(positionX, positionY, 0, getMaxHorizontal(), 0, getMaxVertical());
    }
  }

  @CheckResult int getMaxHorizontal() {
    final Drawable drawable = getDrawable();
    if (drawable == null) {
      Timber.e("Drawable is NULL, return 0 horizontal");
      return 0;
    } else {
      final int width = drawable.getBounds().width();
      return Math.abs(width - screenW);
    }
  }

  @CheckResult int getMaxVertical() {
    final Drawable drawable = getDrawable();
    if (drawable == null) {
      Timber.e("Drawable is NULL, return 0 vertical");
      return 0;
    } else {
      final int width = drawable.getBounds().height();
      return Math.abs(width - screenH);
    }
  }

  static class FinalizedScrollComponents {
    @Nullable private GestureDetectorCompat gestureDetector;
    @Nullable private OverScroller overScroller;

    @NonNull @CheckResult OverScroller getOverScroller() {
      if (overScroller == null) {
        throw new NullPointerException("OverScroller is NULL");
      }
      return overScroller;
    }

    void setOverScroller(@NonNull OverScroller overScroller) {
      if (this.overScroller == null) {
        this.overScroller = overScroller;
      } else {
        throw new RuntimeException("OverScroller is already assigned, cannot assign again");
      }
    }

    @NonNull @CheckResult GestureDetectorCompat getGestureDetector() {
      if (gestureDetector == null) {
        throw new NullPointerException("GestureDetector is NULL");
      }
      return gestureDetector;
    }

    void setGestureDetector(@NonNull GestureDetectorCompat gestureDetector) {
      if (this.gestureDetector == null) {
        this.gestureDetector = gestureDetector;
      } else {
        throw new RuntimeException("GestureDetector is already assigned, cannot assign again");
      }
    }
  }

  class ScrollingGestureListener extends SimpleOnGestureListener {

    @NonNull private final ImageView view;

    ScrollingGestureListener(@NonNull ImageView view) {
      this.view = view;
    }

    @Override public boolean onDown(MotionEvent e) {
      scrollComponents.getOverScroller().forceFinished(true);
      ViewCompat.postInvalidateOnAnimation(view);
      return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
      final OverScroller overScroller = scrollComponents.getOverScroller();
      overScroller.forceFinished(true);
      overScroller.fling(positionX, positionY, (int) -velocityX, (int) -velocityY, 0,
          getMaxHorizontal(), 0, getMaxVertical());
      ViewCompat.postInvalidateOnAnimation(view);
      return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
      final OverScroller overScroller = scrollComponents.getOverScroller();
      overScroller.forceFinished(true);
      // normalize scrolling distances to not overscroll the image
      int dx = (int) distanceX;
      int dy = (int) distanceY;
      final int newPositionX = positionX + dx;
      final int newPositionY = positionY + dy;
      if (newPositionX < 0) {
        dx -= newPositionX;
      } else if (newPositionX > getMaxHorizontal()) {
        dx -= (newPositionX - getMaxHorizontal());
      }
      if (newPositionY < 0) {
        dy -= newPositionY;
      } else if (newPositionY > getMaxVertical()) {
        dy -= (newPositionY - getMaxVertical());
      }
      overScroller.startScroll(positionX, positionY, dx, dy, 0);
      ViewCompat.postInvalidateOnAnimation(view);
      return true;
    }
  }
}

