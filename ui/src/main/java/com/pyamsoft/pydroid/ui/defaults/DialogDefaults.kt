package com.pyamsoft.pydroid.ui.defaults

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Default values for Dialogs */
public object DialogDefaults {

  /**
   * Elevation for a Dialog
   *
   * NOTE: This elevation value does not match the MD spec because the value of the elevation is
   * used in Dark mode to brighten the color of a Surface.
   *
   * Because the Surface would get overly bright with 24.dp or 16.dp, we set the elevation to this
   * low value.
   *
   * This has the unfortunate side effect of making the shadow on the surface only slightly elevated
   * as well though.
   */
  public val Elevation: Dp = 4.dp
}
