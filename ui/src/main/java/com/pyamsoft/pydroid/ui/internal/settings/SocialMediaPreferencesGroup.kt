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

package com.pyamsoft.pydroid.ui.internal.settings

import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.res.stringResource
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.preference.preference
import com.pyamsoft.pydroid.ui.preference.preferenceGroup

@Composable
@CheckResult
internal fun rememberSocialMediaPreferencesGroup(
    onViewSocialMediaClicked: () -> Unit,
    onViewBlogClicked: () -> Unit,
): Preferences.Group {
  val socialMediaPreference =
      rememberSocialMediaPreference(
          onClick = onViewSocialMediaClicked,
      )
  val blogPreference =
      rememberBlogPreference(
          onClick = onViewBlogClicked,
      )

  val preferences =
      remember(
          socialMediaPreference,
          blogPreference,
      ) {
        mutableListOf<Preferences.Item>().apply {
          add(socialMediaPreference)
          add(blogPreference)
        }
      }

  val title = stringResource(R.string.follow_pyamsoft_around_the_web)
  return remember(
      title,
      preferences,
  ) {
    preferenceGroup(
        id = "social_groups",
        name = title,
        preferences = preferences,
    )
  }
}

@Composable
@CheckResult
private fun rememberSocialMediaPreference(
    onClick: () -> Unit,
): Preferences.Item {
  val name = stringResource(R.string.social_media_f_title)
  val summary = stringResource(R.string.social_media_f_summary)

  val handleClick by rememberUpdatedState(onClick)

  return remember(
      name,
      summary,
  ) {
    preference(
        id = "social_media",
        name = name,
        summary = summary,
        onClick = handleClick,
    )
  }
}

@Composable
@CheckResult
private fun rememberBlogPreference(
    onClick: () -> Unit,
): Preferences.Item {
  val name = stringResource(R.string.social_media_b_title)
  val summary = stringResource(R.string.social_media_b_summary)

  val handleClick by rememberUpdatedState(onClick)

  return remember(
      name,
      summary,
  ) {
    preference(
        id = "blog",
        name = name,
        summary = summary,
        onClick = handleClick,
    )
  }
}
