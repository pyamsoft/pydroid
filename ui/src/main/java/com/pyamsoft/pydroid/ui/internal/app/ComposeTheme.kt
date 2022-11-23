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

package com.pyamsoft.pydroid.ui.internal.app

import android.app.Activity
import androidx.compose.runtime.Composable
import com.pyamsoft.pydroid.ui.app.ComposeThemeProvider
import com.pyamsoft.pydroid.ui.app.invoke
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.theme.asThemeProvider

/** Alias for a theme type */
internal fun interface ComposeTheme {

  /** Renders content() with the provided compose theme */
  @Composable
  fun Render(
      activity: Activity,
      content: @Composable () -> Unit,
  )
}

/** Convenience method for rendering a theme provider */
@Composable
internal operator fun ComposeTheme.invoke(
    activity: Activity,
    content: @Composable () -> Unit,
) {
  val self = this
  self.Render(activity, content)
}

/** Generates a ComposeTheme */
internal class ComposeThemeFactory(
    private val theming: Theming,
    private val themeProvider: ComposeThemeProvider,
) : ComposeTheme {

  @Composable
  override fun Render(activity: Activity, content: @Composable () -> Unit) {
    val provider = activity.asThemeProvider(theming)
    themeProvider(
        activity = activity,
        themeProvider = provider,
        content = content,
    )
  }
}
