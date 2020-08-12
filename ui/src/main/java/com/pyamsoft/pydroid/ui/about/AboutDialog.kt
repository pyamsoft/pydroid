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

package com.pyamsoft.pydroid.ui.about

import androidx.fragment.app.Fragment
import com.pyamsoft.pydroid.ui.app.dialog.FullscreenThemeDialog

internal class AboutDialog : FullscreenThemeDialog() {

    override fun getContents(): Fragment {
        return AboutFragment.newInstance()
    }

    override fun getInitialTitle(): String {
        return "Open Source Licenses"
    }

    companion object {

        internal const val TAG = "AboutDialog"
    }
}
