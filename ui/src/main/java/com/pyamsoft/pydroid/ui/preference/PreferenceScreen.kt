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

package com.pyamsoft.pydroid.ui.preference

import androidx.annotation.CheckResult
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.pyamsoft.pydroid.theme.ZeroSize
import com.pyamsoft.pydroid.ui.util.rememberAsStateList

/** This stable class must be used to avoid Recompositions */
@Stable
public data class PreferenceScreenData(
    /** The list of preferences */
    public val preferences: List<Preferences>
)

/** Extension function for easy usage */
@CheckResult
public fun List<Preferences>.asScreenData(): PreferenceScreenData {
  return PreferenceScreenData(this)
}

/** Create a screen that hosts Preference Composables */
@Composable
public fun PreferenceScreen(
    modifier: Modifier = Modifier,
    topItemMargin: Dp = ZeroSize,
    bottomItemMargin: Dp = ZeroSize,
    preferences: PreferenceScreenData,
) {
  val data = preferences.preferences.rememberAsStateList()

  LazyColumn(
      modifier = modifier,
  ) {
    if (topItemMargin > ZeroSize) {
      item {
        Spacer(
            modifier = Modifier.fillMaxWidth().height(topItemMargin),
        )
      }
    }

    data.forEach { preference ->
      when (preference) {
        is Preferences.Group ->
            renderGroupInScope(
                modifier = Modifier.fillMaxWidth(),
                preference = preference,
            )
        is Preferences.Item ->
            renderItemInScope(
                modifier = Modifier.fillMaxWidth(),
                preference = preference,
            )
      }
    }

    if (bottomItemMargin > ZeroSize) {
      item {
        Spacer(
            modifier = Modifier.fillMaxWidth().height(bottomItemMargin),
        )
      }
    }
  }
}

private fun LazyListScope.renderGroupInScope(
    modifier: Modifier = Modifier,
    preference: Preferences.Group
) {
  val name = preference.name
  val preferences = preference.preferences
  val isEnabled = preference.isEnabled

  item {
    PreferenceGroupHeader(
        modifier = Modifier.fillMaxWidth(),
        name = name,
    )
  }

  items(
      items = preferences,
      key = { it.renderKey },
  ) { item ->
    CompositionLocalProvider(
        LocalPreferenceEnabledStatus provides isEnabled,
    ) {
      RenderItem(
          modifier = modifier,
          preference = item,
      )
    }
  }
}

private fun LazyListScope.renderItemInScope(
    modifier: Modifier = Modifier,
    preference: Preferences.Item
) {
  item {
    RenderItem(
        modifier = modifier,
        preference = preference,
    )
  }
}

@Composable
private fun RenderItem(
    modifier: Modifier = Modifier,
    preference: Preferences.Item,
) {
  return when (preference) {
    is Preferences.SimplePreference ->
        SimplePreferenceItem(
            modifier = modifier,
            preference = preference,
        )
    is Preferences.SwitchPreference ->
        SwitchPreferenceItem(
            modifier = modifier,
            preference = preference,
        )
    is Preferences.CheckBoxPreference ->
        CheckBoxPreferenceItem(
            modifier = modifier,
            preference = preference,
        )
    is Preferences.ListPreference ->
        ListPreferenceItem(
            modifier = modifier,
            preference = preference,
        )
    is Preferences.InAppPreference ->
        InAppPreferenceItem(
            modifier = modifier,
            preference = preference,
        )
    is Preferences.CustomPreference ->
        CustomPreferenceItem(
            modifier = modifier,
            preference = preference,
        )
    else ->
        throw IllegalArgumentException(
            "Preference is not a consumable type for PreferenceScreen: $preference")
  }
}

@Composable
private fun PreviewPreferenceScreen(isEnabled: Boolean) {
  PreferenceScreen(
      preferences =
          remember {
            listOf(
                    preferenceGroup(
                        name = "TEST",
                        isEnabled = isEnabled,
                        preferences =
                            listOf(
                                preference(
                                    name = "TEST ITEM 1",
                                ),
                                preference(
                                    name = "TEST ITEM 2",
                                    summary = "TESTING 123",
                                ),
                                inAppPreference(
                                    name = "TEST IN-APP",
                                ),
                                checkBoxPreference(
                                    name = "TEST CHECKBOX 1",
                                    checked = false,
                                    onCheckedChanged = {},
                                ),
                                checkBoxPreference(
                                    name = "TEST CHECKBOX 2",
                                    checked = true,
                                    onCheckedChanged = {},
                                ),
                                switchPreference(
                                    name = "TEST SWITCH 1",
                                    checked = false,
                                    onCheckedChanged = {},
                                ),
                                switchPreference(
                                    name = "TEST SWITCH 2",
                                    checked = true,
                                    onCheckedChanged = {},
                                ),
                            ),
                    ),
                )
                .asScreenData()
          },
  )
}

@Preview
@Composable
private fun PreviewPreferenceScreenEnabled() {
  PreviewPreferenceScreen(isEnabled = true)
}

@Preview
@Composable
private fun PreviewPreferenceScreenDisabled() {
  PreviewPreferenceScreen(isEnabled = false)
}
