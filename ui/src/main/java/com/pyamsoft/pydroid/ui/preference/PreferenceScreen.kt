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

package com.pyamsoft.pydroid.ui.preference

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
@JvmOverloads
public fun PreferenceScreen(
    modifier: Modifier = Modifier,
    preferences: List<Preferences>,
) {
  LazyColumn(modifier = modifier, contentPadding = PaddingValues(vertical = 8.dp)) {
    preferences.forEach { preference ->
      when (preference) {
        is Preferences.Group -> renderGroup(preference)
        is Preferences.Item -> renderItem(preference)
      }
    }
  }
}

private fun LazyListScope.renderGroup(preference: Preferences.Group) {
  val name = preference.name
  val preferences = preference.preferences
  val isEnabled = preference.isEnabled

  item { PreferenceGroupHeader(name) }

  items(items = preferences, key = { it.renderKey }) { item ->
    CompositionLocalProvider(
        LocalPreferenceEnabledStatus provides isEnabled,
    ) { RenderItem(item) }
  }
}

private fun LazyListScope.renderItem(preference: Preferences.Item) {
  item { RenderItem(preference) }
}

@Composable
private fun RenderItem(
    preference: Preferences.Item,
) {
  return when (preference) {
    is Preferences.SimplePreference -> SimplePreferenceItem(preference)
    is Preferences.SwitchPreference -> SwitchPreferenceItem(preference)
    is Preferences.CheckBoxPreference -> CheckBoxPreferenceItem(preference)
    is Preferences.ListPreference -> ListPreferenceItem(preference)
    is Preferences.InAppPreference -> InAppPreferenceItem(preference)
    is Preferences.AdPreference -> AdPreferenceItem(preference)
    else ->
        throw IllegalArgumentException(
            "Preference is not a consumable type for PreferenceScreen: $preference")
  }
}

@Composable
private fun PreviewPreferenceScreen(isEnabled: Boolean) {
  Surface {
    PreferenceScreen(
        preferences =
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
                            adPreference(
                                name = "TEST AD",
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
            ),
    )
  }
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
