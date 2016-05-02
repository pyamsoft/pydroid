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

public abstract class HoleOverlay {
  private int backgroundColor;
  private float holeRadius;
  private float xPos;
  private float yPos;
  private int transparency;

  public HoleOverlay() {
  }

  public void setBackgroundColor(int backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  public void setTransparency(int transparency) {
    this.transparency = transparency;
  }

  void setHoleRadius(float holeRadius) {
    this.holeRadius = holeRadius;
  }

  void setX(float x) {
    xPos = x;
  }

  void setY(float y) {
    yPos = y;
  }

  float getHoleRadius() {
    return holeRadius;
  }

  float getX() {
    return xPos;
  }

  float getY() {
    return yPos;
  }

  int getBackgroundColor() {
    return backgroundColor;
  }

  int getTransparency() {
    return transparency;
  }

  abstract void draw(Canvas c, int width, int height);
}

