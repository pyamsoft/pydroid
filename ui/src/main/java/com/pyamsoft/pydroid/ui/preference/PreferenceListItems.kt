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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.AlertDialog
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.defaults.ListItemDefaults
import com.pyamsoft.pydroid.ui.internal.app.AdBadge
import com.pyamsoft.pydroid.ui.internal.app.InAppBadge

@Composable
internal fun SimplePreferenceItem(
    modifier: Modifier = Modifier,
    preference: Preferences.SimplePreference,
) {
  val isEnabled = preference.isEnabled
  val name = preference.name
  val summary = preference.summary
  val icon = preference.icon
  val onClick = preference.onClick

  PreferenceItem(
      isEnabled = isEnabled,
      text = name,
      summary = summary,
      icon = icon,
      modifier = { enabled -> modifier.clickable(enabled = enabled) { onClick?.invoke() } },
  )
}

@Composable
internal fun CustomPreferenceItem(
    modifier: Modifier = Modifier,
    preference: Preferences.CustomPreference,
) {
  val isEnabled = preference.isEnabled
  val name = preference.name
  val summary = preference.summary
  val icon = preference.icon
  val content = preference.content

  PreferenceItem(
      isEnabled = isEnabled,
      text = name,
      summary = summary,
      icon = icon,
      badge = { AdBadge() },
      modifier = { modifier },
      customContent = content,
  )
}

@Composable
internal fun AdPreferenceItem(
    modifier: Modifier = Modifier,
    preference: Preferences.AdPreference,
) {
  val isEnabled = preference.isEnabled
  val name = preference.name
  val summary = preference.summary
  val icon = preference.icon
  val onClick = preference.onClick

  PreferenceItem(
      isEnabled = isEnabled,
      text = name,
      summary = summary,
      icon = icon,
      badge = { AdBadge() },
      modifier = { enabled -> modifier.clickable(enabled = enabled) { onClick?.invoke() } },
  )
}

@Composable
internal fun InAppPreferenceItem(
    modifier: Modifier = Modifier,
    preference: Preferences.InAppPreference,
) {
  val isEnabled = preference.isEnabled
  val name = preference.name
  val summary = preference.summary
  val icon = preference.icon
  val onClick = preference.onClick

  PreferenceItem(
      isEnabled = isEnabled,
      text = name,
      summary = summary,
      icon = icon,
      badge = { InAppBadge() },
      modifier = { enabled -> modifier.clickable(enabled = enabled) { onClick?.invoke() } },
  )
}

@Composable
internal fun CheckBoxPreferenceItem(
    modifier: Modifier = Modifier,
    preference: Preferences.CheckBoxPreference,
) {
  val isEnabled = preference.isEnabled
  val name = preference.name
  val summary = preference.summary
  val icon = preference.icon
  val onCheckedChanged = preference.onCheckedChanged
  val checked = preference.checked

  PreferenceItem(
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
        modifier.clickable(enabled = enabled) { onCheckedChanged(!checked) }
      },
  )
}

@Composable
internal fun SwitchPreferenceItem(
    modifier: Modifier = Modifier,
    preference: Preferences.SwitchPreference,
) {
  val isEnabled = preference.isEnabled
  val name = preference.name
  val summary = preference.summary
  val icon = preference.icon
  val onCheckedChanged = preference.onCheckedChanged
  val checked = preference.checked

  PreferenceItem(
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
        modifier.clickable(enabled = enabled) { onCheckedChanged(!checked) }
      },
  )
}

@Composable
internal fun ListPreferenceItem(
    modifier: Modifier = Modifier,
    preference: Preferences.ListPreference,
) {
  val (isDialogShown, showDialog) = remember { mutableStateOf(false) }

  val isEnabled = preference.isEnabled
  val title = preference.name
  val summary = preference.summary
  val icon = preference.icon
  val currentValue = preference.value
  val entries = preference.entries
  val onPreferenceSelected = preference.onPreferenceSelected

  PreferenceItem(
      isEnabled = isEnabled,
      text = title,
      summary = summary,
      icon = icon,
      modifier = { enabled ->
        modifier.clickable(enabled = enabled) { showDialog(!isDialogShown) }
      },
  )

  if (isDialogShown) {
    AlertDialog(
        onDismissRequest = { showDialog(false) },
        title = {
          Text(
              text = title,
              style = MaterialTheme.typography.h6,
          )
        },
        buttons = {
          Column(
              modifier = Modifier.padding(MaterialTheme.keylines.content),
          ) {
            entries.forEach { current ->
              val name = current.key
              val value = current.value
              val isSelected = value == currentValue
              val onEntrySelected = {
                onPreferenceSelected(name, value)
                showDialog(false)
              }

              Row(
                  modifier =
                      Modifier.fillMaxWidth()
                          .selectable(
                              selected = isSelected,
                              onClick = {
                                if (!isSelected) {
                                  onEntrySelected()
                                }
                              },
                          )
                          .padding(MaterialTheme.keylines.content),
                  verticalAlignment = Alignment.CenterVertically,
              ) {
                RadioButton(
                    modifier = Modifier.padding(end = MaterialTheme.keylines.content),
                    selected = isSelected,
                    onClick = {
                      if (!isSelected) {
                        onEntrySelected()
                      }
                    },
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.body1,
                )
              }
            }

            Row(
                modifier = Modifier.padding(top = MaterialTheme.keylines.baseline).fillMaxWidth(),
            ) {
              Spacer(
                  modifier = Modifier.weight(1F),
              )

              TextButton(
                  onClick = { showDialog(false) },
              ) {
                Text(
                    text = stringResource(R.string.close),
                )
              }
            }
          }
        },
    )
  }
}

@Composable
private fun PreferenceItem(
    isEnabled: Boolean,
    text: String,
    summary: String,
    icon: ImageVector?,
    modifier: (isEnabled: Boolean) -> Modifier,
    trailing: (@Composable (isEnabled: Boolean) -> Unit)? = null,
    badge: (@Composable () -> Unit)? = null,
    customContent: (@Composable (isEnabled: Boolean) -> Unit)? = null,
) {
  val enabled = LocalPreferenceEnabledStatus.current && isEnabled

  PreferenceAlphaWrapper(
      isEnabled = enabled,
  ) {
    if (customContent == null) {
      DefaultPreferenceItem(
          enabled = enabled,
          text = text,
          summary = summary,
          icon = icon,
          modifier = modifier,
          trailing = trailing,
          badge = badge,
      )
    } else {
      customContent(enabled)
    }
  }
}

@Composable
private fun DefaultPreferenceItem(
    enabled: Boolean,
    text: String,
    summary: String,
    icon: ImageVector?,
    modifier: (isEnabled: Boolean) -> Modifier,
    trailing: (@Composable (isEnabled: Boolean) -> Unit)? = null,
    badge: (@Composable () -> Unit)? = null,
) {
  Row(
      modifier = modifier(enabled).padding(all = MaterialTheme.keylines.baseline),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Start,
  ) {
    Box(
        modifier = Modifier.size(ListItemDefaults.LeadingSize),
        contentAlignment = Alignment.Center,
    ) {
      if (icon != null) {
        val imageTintColor = if (MaterialTheme.colors.isLight) Color.Black else Color.White
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = imageTintColor,
        )
      }
    }

    Column(
        modifier = Modifier.padding(start = MaterialTheme.keylines.baseline).weight(1F),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
      Row(
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
            text = text,
            style = MaterialTheme.typography.body1,
        )
        badge?.let { compose ->
          Box(
              modifier = Modifier.padding(start = MaterialTheme.keylines.baseline),
          ) { compose() }
        }
        Spacer(modifier = Modifier.weight(1F))
      }

      if (summary.isNotBlank()) {
        Box(
            modifier =
                Modifier.padding(
                    top = MaterialTheme.keylines.baseline,
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
          modifier =
              Modifier.padding(start = MaterialTheme.keylines.baseline)
                  .size(ListItemDefaults.LeadingSize),
          contentAlignment = Alignment.Center,
      ) { compose(enabled) }
    }
  }
}
