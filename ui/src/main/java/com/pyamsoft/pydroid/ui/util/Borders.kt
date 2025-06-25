/*
 * Copyright 2025 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.ui.util

import androidx.annotation.CheckResult
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp

/**
 * Lifted and modified from
 *
 * https://stackoverflow.com/questions/74740835/how-to-draw-border-around-the-lazycolumn-items-in-android-compose
 *
 * Changes:
 * - Code style and formatting
 * - Remember for px calculations from density
 * - Stroke width was drawing "half size", double up
 * - drawWithContent instead of drawBehind to apply the border "over" content, like how it would in
 *   Surface
 */

/** Add only a Top Border */
@CheckResult
public fun Modifier.topBorder(
    strokeWidth: Dp,
    color: Color,
    cornerRadius: Dp,
): Modifier {
  return composed(
      factory = {
        val strokeWidthPx = rememberPx(strokeWidth)
        val cornerRadiusPx = rememberPx(cornerRadius)

        drawWithContent {
          val width = size.width
          val height = size.height

          drawContent()

          drawLine(
              color = color,
              start =
                  Offset(
                      x = 0f,
                      y = height,
                  ),
              end =
                  Offset(
                      x = 0f,
                      y = cornerRadiusPx,
                  ),
              strokeWidth = strokeWidthPx,
          )

          drawArc(
              color = color,
              startAngle = 180F,
              sweepAngle = 90F,
              useCenter = false,
              topLeft = Offset.Zero,
              size =
                  Size(
                      cornerRadiusPx * 2,
                      cornerRadiusPx * 2,
                  ),
              style =
                  Stroke(
                      width = strokeWidthPx,
                  ),
          )

          drawLine(
              color = color,
              start =
                  Offset(
                      x = cornerRadiusPx,
                      y = 0F,
                  ),
              end =
                  Offset(
                      x = width - cornerRadiusPx,
                      y = 0F,
                  ),
              strokeWidth = strokeWidthPx,
          )

          drawArc(
              color = color,
              startAngle = 270F,
              sweepAngle = 90F,
              useCenter = false,
              topLeft =
                  Offset(
                      x = width - cornerRadiusPx * 2,
                      y = 0F,
                  ),
              size =
                  Size(
                      cornerRadiusPx * 2,
                      cornerRadiusPx * 2,
                  ),
              style =
                  Stroke(
                      width = strokeWidthPx,
                  ),
          )

          drawLine(
              color = color,
              start =
                  Offset(
                      x = width,
                      y = height,
                  ),
              end =
                  Offset(
                      x = width,
                      y = cornerRadiusPx,
                  ),
              strokeWidth = strokeWidthPx,
          )
        }
      },
  )
}

/** Add only a bottom Border */
@CheckResult
public fun Modifier.bottomBorder(
    strokeWidth: Dp,
    color: Color,
    cornerRadius: Dp,
): Modifier {
  // For some reason the stroke width is too thin, about half as much, double it up

  return composed(
      factory = {
        val strokeWidthPx = rememberPx(strokeWidth)
        val cornerRadiusPx = rememberPx(cornerRadius)

        drawWithContent {
          val width = size.width
          val height = size.height

          drawContent()

          drawLine(
              color = color,
              start = Offset.Zero,
              end =
                  Offset(
                      x = 0F,
                      y = height - cornerRadiusPx,
                  ),
              strokeWidth = strokeWidthPx,
          )

          drawArc(
              color = color,
              startAngle = 90F,
              sweepAngle = 90F,
              useCenter = false,
              topLeft =
                  Offset(
                      x = 0F,
                      y = height - cornerRadiusPx * 2,
                  ),
              size =
                  Size(
                      cornerRadiusPx * 2,
                      cornerRadiusPx * 2,
                  ),
              style =
                  Stroke(
                      width = strokeWidthPx,
                  ),
          )

          drawLine(
              color = color,
              start =
                  Offset(
                      x = cornerRadiusPx,
                      y = height,
                  ),
              end =
                  Offset(
                      x = width - cornerRadiusPx,
                      y = height,
                  ),
              strokeWidth = strokeWidthPx,
          )

          drawArc(
              color = color,
              startAngle = 0F,
              sweepAngle = 90F,
              useCenter = false,
              topLeft =
                  Offset(
                      x = width - cornerRadiusPx * 2,
                      y = height - cornerRadiusPx * 2,
                  ),
              size =
                  Size(
                      cornerRadiusPx * 2,
                      cornerRadiusPx * 2,
                  ),
              style =
                  Stroke(
                      width = strokeWidthPx,
                  ),
          )

          drawLine(
              color = color,
              start =
                  Offset(
                      x = width,
                      y = 0f,
                  ),
              end =
                  Offset(
                      x = width,
                      y = height - cornerRadiusPx,
                  ),
              strokeWidth = strokeWidthPx,
          )
        }
      },
  )
}

/** Add borders to both sides */
@CheckResult
public fun Modifier.sideBorders(
    strokeWidth: Dp,
    color: Color,
): Modifier =
    this.rightBorder(
            strokeWidth = strokeWidth,
            color = color,
        )
        .leftBorder(
            strokeWidth = strokeWidth,
            color = color,
        )

/** Add borders to the left, regardless of LTR setting */
@CheckResult
public fun Modifier.leftBorder(
    strokeWidth: Dp,
    color: Color,
): Modifier {
  return composed(
      factory = {
        val strokeWidthPx = rememberPx(strokeWidth)

        drawWithContent {
          val height = size.height

          drawContent()

          drawLine(
              color = color,
              start = Offset.Zero,
              end =
                  Offset(
                      x = 0F,
                      y = height,
                  ),
              strokeWidth = strokeWidthPx,
          )
        }
      },
  )
}

/** Add borders to the right, regardless of LTR setting */
@CheckResult
public fun Modifier.rightBorder(
    strokeWidth: Dp,
    color: Color,
): Modifier {
  return composed(
      factory = {
        val strokeWidthPx = rememberPx(strokeWidth)

        drawWithContent {
          val width = size.width
          val height = size.height

          drawContent()

          drawLine(
              color = color,
              start =
                  Offset(
                      x = width,
                      y = 0f,
                  ),
              end =
                  Offset(
                      x = width,
                      y = height,
                  ),
              strokeWidth = strokeWidthPx,
          )
        }
      },
  )
}
