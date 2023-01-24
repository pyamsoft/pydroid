/*
 * Copyright 2023 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.app

import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy

/**
 * Compose Dialog is currently a little buggy.
 *
 * Using the default usePlatformDefaultWidth field causes the dialog to be sized incorrectly. This
 * helper function provides a DialogProperties object that always unsets this field to avoid these
 * dialog sizing bugs.
 */
@Composable
@CheckResult
@OptIn(ExperimentalComposeUiApi::class)
public fun rememberDialogProperties(
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    securePolicy: SecureFlagPolicy = SecureFlagPolicy.Inherit,
): DialogProperties {
  return remember(
      dismissOnBackPress,
      dismissOnClickOutside,
      securePolicy,
  ) {
    DialogProperties(
        dismissOnBackPress = dismissOnBackPress,
        dismissOnClickOutside = dismissOnClickOutside,
        securePolicy = securePolicy,
        usePlatformDefaultWidth = false,
    )
  }
}
