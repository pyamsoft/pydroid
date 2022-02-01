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

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsHeight
import com.pyamsoft.pydroid.ui.theme.ZeroElevation
import com.pyamsoft.pydroid.ui.theme.ZeroSpacing

@Composable
public fun PreferenceScreen(
    modifier: Modifier = Modifier,
    elevation: Dp = ZeroElevation,
    topItemMargin: Dp = ZeroSpacing,
    bottomItemMargin: Dp = ZeroSpacing,
    preferences: List<Preferences>,
) {
  Surface(
      modifier = modifier,
      elevation = elevation,
  ) {
    LazyColumn {
      val maxIndex = preferences.lastIndex
      preferences.forEachIndexed { index, preference ->
        when (preference) {
          is Preferences.Group ->
              renderGroupInScope(
                  modifier = Modifier.fillMaxWidth(),
                  listScope = this,
                  topItemMargin = topItemMargin,
                  bottomItemMargin = bottomItemMargin,
                  index = index,
                  maxIndex = maxIndex,
                  preference = preference,
              )
          is Preferences.Item ->
              renderItemInScope(
                  modifier = Modifier.fillMaxWidth(),
                  listScope = this,
                  topItemMargin = topItemMargin,
                  bottomItemMargin = bottomItemMargin,
                  index = index,
                  maxIndex = maxIndex,
                  preference = preference,
              )
        }
      }
    }
  }
}

// NOTE(Peter): Do not extend anything with LazyListScope.() -> Unit, or else
// calling applications will break with a NoClassDefFoundError
//
// Don't do private fun LazyListScope.FunctionName()
// or
// content: LazyListScope.() -> Unit
//
// until this is fixed.
private fun renderPaddedItemsInScope(
    listScope: LazyListScope,
    topItemMargin: Dp,
    bottomItemMargin: Dp,
    index: Int,
    maxIndex: Int,
    content: () -> Unit
) {
  if (index == 0) {
    listScope.item {
      Spacer(
          modifier =
              Modifier.fillMaxWidth()
                  .statusBarsHeight(
                      additional = topItemMargin,
                  ),
      )
    }
  }

  content()

  if (index == maxIndex) {
    listScope.item {
      Spacer(
          modifier =
              Modifier.fillMaxWidth()
                  .navigationBarsHeight(
                      additional = bottomItemMargin,
                  ),
      )
    }
  }
}

// NOTE(Peter): Do not extend anything with LazyListScope.() -> Unit, or else
// calling applications will break with a NoClassDefFoundError
//
// Don't do private fun LazyListScope.FunctionName()
// or
// content: LazyListScope.() -> Unit
//
// until this is fixed.
private fun renderGroupInScope(
    modifier: Modifier = Modifier,
    listScope: LazyListScope,
    topItemMargin: Dp,
    bottomItemMargin: Dp,
    index: Int,
    maxIndex: Int,
    preference: Preferences.Group
) {
  val name = preference.name
  val preferences = preference.preferences
  val isEnabled = preference.isEnabled

  renderPaddedItemsInScope(
      listScope = listScope,
      topItemMargin = topItemMargin,
      bottomItemMargin = bottomItemMargin,
      index = index,
      maxIndex = maxIndex,
  ) {
    listScope.item {
      PreferenceGroupHeader(
          modifier = Modifier.fillMaxWidth(),
          name = name,
      )
    }

    listScope.items(
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
}

// NOTE(Peter): Do not extend anything with LazyListScope.() -> Unit, or else
// calling applications will break with a NoClassDefFoundError
//
// Don't do private fun LazyListScope.FunctionName()
// or
// content: LazyListScope.() -> Unit
//
// until this is fixed.
private fun renderItemInScope(
    modifier: Modifier = Modifier,
    listScope: LazyListScope,
    topItemMargin: Dp,
    bottomItemMargin: Dp,
    index: Int,
    maxIndex: Int,
    preference: Preferences.Item
) {
  renderPaddedItemsInScope(
      listScope = listScope,
      topItemMargin = topItemMargin,
      bottomItemMargin = bottomItemMargin,
      index = index,
      maxIndex = maxIndex,
  ) {
    listScope.item {
      RenderItem(
          modifier = modifier,
          preference = preference,
      )
    }
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
    is Preferences.AdPreference ->
        AdPreferenceItem(
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
