/*
 * Copyright 2023 pyamsoft
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

package com.pyamsoft.pydroid.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import com.pyamsoft.pydroid.ui.internal.util.rememberPYDroidDelegate

/**
 * Shows the default UI for when a billing upsell
 *
 * Must be hosted in a PYDroidActivity
 */
@Composable
public fun BillingUpsellWidget(
    modifier: Modifier = Modifier,
    dialogModifier: Modifier = Modifier,
) {
  // If isEditMode, we don't render nothing
  if (LocalInspectionMode.current) {
    return
  }

  val delegate = rememberPYDroidDelegate()
  val billing = remember(delegate) { delegate.billingUpsell() }

  billing.RenderBillingUpsellWidget(
      modifier = modifier,
      dialogModifier = dialogModifier,
  )
}
