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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import com.pyamsoft.pydroid.R;
import timber.log.Timber;

public final class GradientHoleDrawer extends HoleOverlay {

  @NonNull private final float[] gradientStops;

  @NonNull private final Paint paint;

  public GradientHoleDrawer(final View target) {

    // Set default color based on activity
    final TypedValue typedValue = new TypedValue();
    target.getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
    final int primaryColor = typedValue.data;
    setBackgroundColor(primaryColor);

    // Default is Opaque
    setTransparency(255);

    gradientStops = new float[] { 0f, 0.95f, 1.0f };
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setStyle(Paint.Style.FILL);

    final int[] location = new int[2];
    target.getLocationOnScreen(location);
    final float yCenter = (location[1] - ((float) target.getHeight() / 2));

    setX(location[0]);
    setY(yCenter);

    final float radius = (float) target.getWidth() * 0.75F;
    setHoleRadius(radius);

    Timber.d("X: %f", getX());
    Timber.d("Y: %f", getY());
    Timber.d("Radius: %f", getHoleRadius());
  }

  private void prepareShader() {
    Timber.d("Prepare Shader");
    final int[] colors = {
        Color.TRANSPARENT, Color.TRANSPARENT, getBackgroundColor()
    };

    final RadialGradient shader =
        new RadialGradient(getX(), getY(), getHoleRadius(), colors, gradientStops,
            Shader.TileMode.CLAMP);
    paint.setAlpha(getTransparency());
    paint.setShader(shader);
  }

  @Override void draw(Canvas c, int width, int height) {
    prepareShader();

    Timber.d("Draw to canvas");
    c.drawRect(0, 0, width, height, paint);
  }
}
