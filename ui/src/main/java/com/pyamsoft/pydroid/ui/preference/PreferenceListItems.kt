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

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.coil.CoilImage

@Composable
internal fun SimpleListItem(
    preference: Preferences.SimplePreference,
) {
  val isEnabled = preference.isEnabled
  val name = preference.name
  val summary = preference.summary
  val icon = preference.icon
  val onClick = preference.onClick

  ListItem(
      isEnabled = isEnabled,
      text = name,
      summary = summary,
      icon = icon,
      trailing = null,
      modifier = { enabled -> Modifier.clickable(enabled = enabled) { onClick?.invoke() } },
  )
}

@Composable
internal fun CheckBoxListItem(
    preference: Preferences.CheckBoxPreference,
) {
  val isEnabled = preference.isEnabled
  val name = preference.name
  val summary = preference.summary
  val icon = preference.icon
  val onCheckedChanged = preference.onCheckedChanged
  val checked = preference.checked

  ListItem(
      isEnabled = isEnabled,
      text = name,
      summary = summary,
      icon = icon,
      trailing = { enabled ->
        Checkbox(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChanged,
        )
      },
      modifier = { enabled ->
        Modifier.clickable(enabled = enabled) { onCheckedChanged(!checked) }
      },
  )
}

@Composable
internal fun SwitchListItem(
    preference: Preferences.SwitchPreference,
) {
  val isEnabled = preference.isEnabled
  val name = preference.name
  val summary = preference.summary
  val icon = preference.icon
  val onCheckedChanged = preference.onCheckedChanged
  val checked = preference.checked

  ListItem(
      isEnabled = isEnabled,
      text = name,
      summary = summary,
      icon = icon,
      trailing = { enabled ->
        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChanged,
        )
      },
      modifier = { enabled ->
        Modifier.clickable(enabled = enabled) { onCheckedChanged(!checked) }
      },
  )
}

@Composable
private fun ListItem(
    isEnabled: Boolean,
    text: String,
    summary: String,
    @DrawableRes icon: Int,
    trailing: (@Composable (isEnabled: Boolean) -> Unit)?,
    modifier: (isEnabled: Boolean) -> Modifier,
) {
  val enabled = LocalPreferenceEnabledStatus.current && isEnabled

  PreferenceAlphaWrapper(
      isEnabled = enabled,
  ) {
    Row(
        modifier = modifier(enabled).fillMaxWidth().height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
      Box(
          modifier = Modifier.size(48.dp),
          contentAlignment = Alignment.Center,
      ) {
        if (icon != 0) {
          CoilImage(
              modifier = Modifier.size(24.dp),
              imageModel = icon,
          )
        }
      }

      Column(
          modifier = Modifier.weight(1F),
          verticalArrangement = Arrangement.Top,
          horizontalAlignment = Alignment.Start,
      ) {
        Text(
            text = text,
            maxLines = 1,
            style = MaterialTheme.typography.body1,
        )

        if (summary.isNotBlank()) {
          Box(
              modifier =
                  Modifier.padding(
                      top = 4.dp,
                  ),
          ) {
            Text(
                text = summary,
                style = MaterialTheme.typography.caption,
            )
          }
        }
      }

      trailing?.also { compose ->
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center,
        ) { compose(enabled) }
      }
    }
  }
}
