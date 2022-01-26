/*
 * Copyright 2022 Peter Kenji Yamanaka
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

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.runtime.Composable
import com.pyamsoft.pydroid.ui.theme.ThemeProvider
import com.pyamsoft.pydroid.ui.theme.Theming

/** Alias for a theme type */
internal fun interface ComposeTheme {

  /** Must be named "invoke" to work with Kotlin function calling */
  @Composable
  @SuppressLint("ComposableNaming")
  operator fun invoke(
      activity: Activity,
      content: @Composable () -> Unit,
  )
}

/** Composable theme consumer */
public fun interface ComposeThemeProvider {

  /** Must be named "invoke" to work with Kotlin function calling */
  @Composable
  @SuppressLint("ComposableNaming")
  public operator fun invoke(
      themeProvider: ThemeProvider,
      content: @Composable () -> Unit,
  )
}

/** Generates a ComposeTheme */
internal class ComposeThemeFactory(
    private val theming: Theming,
    private val themeProvider: ComposeThemeProvider,
) : ComposeTheme {

  /** Must be named "invoke" to work with Kotlin function calling */
  @Composable
  @SuppressLint("ComposableNaming")
  override operator fun invoke(activity: Activity, content: @Composable () -> Unit) {
    val provider = ThemeProvider { theming.isDarkTheme(activity) }
    themeProvider(themeProvider = provider, content = content)
  }
}
