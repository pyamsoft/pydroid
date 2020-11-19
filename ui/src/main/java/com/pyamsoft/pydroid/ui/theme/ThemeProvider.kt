/*
 * Copyright 2020 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.theme

import android.app.Activity
import androidx.annotation.CheckResult

fun interface ThemeProvider {

    @CheckResult
    fun isDarkTheme(): Boolean
}

@CheckResult
fun Activity.asThemeProvider(theming: Theming): ThemeProvider {
    return ThemeProvider { theming.isDarkTheme(this) }
}

@CheckResult
fun Theming.asThemeProvider(activity: Activity): ThemeProvider {
    return ThemeProvider { this.isDarkTheme(activity) }
}
