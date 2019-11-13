/*
 * Copyright 2019 Peter Kenji Yamanaka
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
 *
 */

package com.pyamsoft.pydroid.ui.rating

import android.text.SpannedString
import androidx.annotation.CheckResult
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes

interface ChangeLogProvider {

    @get:[CheckResult StyleRes]
    val changeLogTheme: Int

    @get:CheckResult
    val changeLogPackageName: String

    @get:CheckResult
    val changelog: SpannedString

    @get:[CheckResult DrawableRes]
    val applicationIcon: Int
}
