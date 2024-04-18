/*
 * Copyright 2024 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
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

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.pyamsoft.pydroid.arch.SaveStateDisposableEffect
import com.pyamsoft.pydroid.core.cast
import com.pyamsoft.pydroid.ui.inject.rememberComposableInjector
import com.pyamsoft.pydroid.ui.internal.preference.PreferenceInjector
import com.pyamsoft.pydroid.ui.internal.preference.PreferenceViewState
import com.pyamsoft.pydroid.ui.theme.ZeroSize
import com.pyamsoft.pydroid.ui.util.collectAsStateMap
import com.pyamsoft.pydroid.ui.util.rememberAsStateList
import com.pyamsoft.pydroid.ui.util.rememberNotNull

private enum class PreferenceScreenContentTypes {
  PREFERENCE,
  TOP_SPACER,
  BOTTOM_SPACER,
  GROUP_HEADER,
}

/** Create a screen that hosts Preference Composables */
@Composable
public fun PreferenceScreen(
    modifier: Modifier = Modifier,
    dialogModifier: Modifier = Modifier,
    topItemMargin: Dp = ZeroSize,
    bottomItemMargin: Dp = ZeroSize,
    preferences: SnapshotStateList<Preferences>,
) {
  val component = rememberComposableInjector { PreferenceInjector() }

  val viewModel = rememberNotNull(component.viewModel)

  SaveStateDisposableEffect(viewModel)

  PreferenceScreenInternal(
      modifier = modifier,
      dialogModifier = dialogModifier,
      topItemMargin = topItemMargin,
      bottomItemMargin = bottomItemMargin,
      preferences = preferences,
      state = viewModel,
      onOpenDialog = { viewModel.handleShowDialog(it) },
      onCloseDialog = { viewModel.handleDismissDialog(it) },
  )
}

@Composable
private fun PreferenceScreenInternal(
    modifier: Modifier = Modifier,
    dialogModifier: Modifier = Modifier,
    state: PreferenceViewState,
    topItemMargin: Dp,
    bottomItemMargin: Dp,
    preferences: SnapshotStateList<Preferences>,
    onOpenDialog: (String) -> Unit,
    onCloseDialog: (String) -> Unit,
) {
  val shownDialogs = state.dialogStates.collectAsStateMap()

  val allPreferences =
      remember(preferences) {
        // Collect all preferences in the group
        val allPreferences = mutableSetOf<Preferences>()
        for (p in preferences) {
          if (p is Preferences.Group) {
            allPreferences.addAll(p.preferences)
          } else {
            allPreferences.add(p)
          }
        }

        return@remember allPreferences
      }

  val dialogPreferences =
      remember(
          shownDialogs,
          allPreferences,
      ) {
        shownDialogs
            .filterValues { it }
            .keys
            .asSequence()
            .mapNotNull { id -> allPreferences.firstOrNull { it.id == id } }
            .mapNotNull { it.cast<Preferences.ListPreference>() }
            .toList()
            .toMutableStateList()
      }

  LazyColumn(
      modifier = modifier,
  ) {
    if (topItemMargin > ZeroSize) {
      item(
          contentType = PreferenceScreenContentTypes.TOP_SPACER,
      ) {
        Spacer(
            modifier = Modifier.fillMaxWidth().height(topItemMargin),
        )
      }
    }

    preferences.forEach { preference ->
      when (preference) {
        is Preferences.Group ->
            renderGroupInScope(
                modifier = Modifier.fillMaxWidth(),
                preference = preference,
                onOpenDialog = onOpenDialog,
            )
        is Preferences.Item ->
            renderItemInScope(
                modifier = Modifier.fillMaxWidth(),
                preference = preference,
                onOpenDialog = onOpenDialog,
            )
      }
    }

    if (bottomItemMargin > ZeroSize) {
      item(
          contentType = PreferenceScreenContentTypes.BOTTOM_SPACER,
      ) {
        Spacer(
            modifier = Modifier.fillMaxWidth().height(bottomItemMargin),
        )
      }
    }
  }

  // Hold dialogs outside of the LazyColumn so that they will mount immediately
  for (pref in dialogPreferences) {
    PreferenceDialog(
        modifier = dialogModifier,
        preference = pref,
        onDismiss = { onCloseDialog(pref.id) },
    )
  }
}

private fun LazyListScope.renderGroupInScope(
    modifier: Modifier = Modifier,
    preference: Preferences.Group,
    onOpenDialog: (String) -> Unit,
) {
  val name = preference.name
  val preferences = preference.preferences
  val isEnabled = preference.isEnabled

  item(
      contentType = PreferenceScreenContentTypes.GROUP_HEADER,
  ) {
    PreferenceGroupHeader(
        modifier = Modifier.fillMaxWidth(),
        name = name,
    )
  }

  items(
      items = preferences,
      key = { it.id },
      contentType = { PreferenceScreenContentTypes.PREFERENCE },
  ) { item ->
    CompositionLocalProvider(
        LocalPreferenceEnabledStatus provides isEnabled,
    ) {
      RenderItem(
          modifier = modifier,
          preference = item,
          onOpenDialog = onOpenDialog,
      )
    }
  }
}

private fun LazyListScope.renderItemInScope(
    modifier: Modifier = Modifier,
    preference: Preferences.Item,
    onOpenDialog: (String) -> Unit,
) {
  item(
      contentType = PreferenceScreenContentTypes.PREFERENCE,
  ) {
    RenderItem(
        modifier = modifier,
        preference = preference,
        onOpenDialog = onOpenDialog,
    )
  }
}

@Composable
private fun RenderItem(
    modifier: Modifier = Modifier,
    preference: Preferences.Item,
    onOpenDialog: (String) -> Unit,
) {
  val id = preference.id

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
            onOpenDialog = { onOpenDialog(id) },
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
  CompositionLocalProvider(
      LocalPreferenceEnabledStatus provides true,
  ) {
    PreferenceScreen(
        preferences =
            listOf(
                    preferenceGroup(
                        id = "TEST",
                        name = "TEST",
                        isEnabled = isEnabled,
                        preferences =
                            listOf(
                                preference(
                                    id = "TEST ITEM 1",
                                    name = "TEST ITEM 1",
                                ),
                                preference(
                                    id = "TEST ITEM 2",
                                    name = "TEST ITEM 2",
                                    summary = "TESTING 123",
                                ),
                                inAppPreference(
                                    id = "TEST IN-APP",
                                    name = "TEST IN-APP",
                                ),
                                checkBoxPreference(
                                    id = "TEST CHECKBOX 1",
                                    name = "TEST CHECKBOX 1",
                                    checked = false,
                                    onCheckedChanged = {},
                                ),
                                checkBoxPreference(
                                    id = "TEST CHECKBOX 2",
                                    name = "TEST CHECKBOX 2",
                                    checked = true,
                                    onCheckedChanged = {},
                                ),
                                switchPreference(
                                    id = "TEST SWITCH 1",
                                    name = "TEST SWITCH 1",
                                    checked = false,
                                    onCheckedChanged = {},
                                ),
                                switchPreference(
                                    id = "TEST SWITCH 2",
                                    name = "TEST SWITCH 2",
                                    checked = true,
                                    onCheckedChanged = {},
                                ),
                            ),
                    ),
                )
                .rememberAsStateList(),
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
