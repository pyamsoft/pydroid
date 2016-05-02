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

package com.pyamsoft.pydroid.onboard;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import timber.log.Timber;

public class HoleView extends LinearLayout {

  private HoleOverlay ovleray;

  public HoleView(Context context) {
    this(context, null);
  }

  public HoleView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public HoleView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    setWillNotDraw(false);
  }

  public final HoleView setDrawer(HoleOverlay drawer) {
    ovleray = drawer;
    invalidate();

    Timber.d("Set catch all onClick");
    setOnClickListener(drawer == null ? null : (OnClickListener) view -> {
      Timber.d("onClick");
      Timber.d("Click consumed");
    });

    return this;
  }

  public void show() {
    ViewCompat.setElevation(this, 30);
    setVisibility(View.VISIBLE);
  }

  public void hide() {
    setVisibility(View.GONE);
    ViewCompat.setElevation(this, 0);
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    if (ovleray != null) {
      Timber.d("onDraw overlay");
      ovleray.draw(canvas, getWidth(), getHeight());
    }
  }
}
