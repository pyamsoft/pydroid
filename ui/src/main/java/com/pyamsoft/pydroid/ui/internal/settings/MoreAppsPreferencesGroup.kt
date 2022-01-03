/*
 * Copyright 2021 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.internal.settings

import androidx.annotation.CheckResult
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.icons.LibraryAdd
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.preference.adPreference
import com.pyamsoft.pydroid.ui.preference.preferenceGroup

@Composable
@CheckResult
internal fun createMoreAppsPreferencesGroup(
    onViewMoreApps: (UriHandler) -> Unit,
): Preferences.Group {
  val uriHandler = LocalUriHandler.current

  return preferenceGroup(
      name = stringResource(R.string.more_apps_from_pyamsoft),
      preferences =
          listOf(
              moreAppsPreference(
                  onViewMoreAppsClicked = { onViewMoreApps(uriHandler) },
              ),
          ),
  )
}

@Composable
@CheckResult
private fun moreAppsPreference(
    onViewMoreAppsClicked: () -> Unit,
): Preferences.Item {
  return adPreference(
      name = stringResource(R.string.more_apps_title),
      summary = stringResource(R.string.rating_summary),
      icon = Icons.Outlined.LibraryAdd,
      onClick = onViewMoreAppsClicked,
  )
}
