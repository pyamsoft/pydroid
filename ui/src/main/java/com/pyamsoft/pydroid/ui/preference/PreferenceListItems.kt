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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Checkbox
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.rememberDialogProperties
import com.pyamsoft.pydroid.ui.defaults.DialogDefaults
import com.pyamsoft.pydroid.ui.defaults.ListItemDefaults
import com.pyamsoft.pydroid.ui.haptics.LocalHapticManager
import com.pyamsoft.pydroid.ui.internal.app.InAppBadge

private enum class PreferenceContentTypes {
  DIALOG_ITEM,
  DIALOG_TITLE,
  DIALOG_ACTION,
}

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
      modifier = modifier.clickable(enabled = isEnabled) { onClick?.invoke() },
      isEnabled = isEnabled,
      text = name,
      summary = summary,
      icon = icon,
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
      modifier = modifier,
      isEnabled = isEnabled,
      text = name,
      summary = summary,
      icon = icon,
      customContent = content,
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
      modifier = modifier.clickable(enabled = isEnabled) { onClick?.invoke() },
      isEnabled = isEnabled,
      text = name,
      summary = summary,
      icon = icon,
      badge = { InAppBadge() },
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
  val onClick = preference.onClick
  val onCheckedChanged = preference.onCheckedChanged
  val checked = preference.checked

  val hapticManager = LocalHapticManager.current

  PreferenceItem(
      modifier = modifier.clickable(enabled = isEnabled) { onClick?.invoke() },
      isEnabled = isEnabled,
      text = name,
      summary = summary,
      icon = icon,
      trailing = { enabled ->
        Checkbox(
            checked = checked,
            enabled = enabled,
            onCheckedChange = { newChecked ->
              if (newChecked) {
                hapticManager?.toggleOn()
              } else {
                hapticManager?.toggleOff()
              }
              onCheckedChanged(newChecked)
            },
        )
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
  val checked = preference.checked
  val onClick = preference.onClick
  val onCheckedChanged = preference.onCheckedChanged

  val hapticManager = LocalHapticManager.current

  PreferenceItem(
      modifier = modifier.clickable(enabled = isEnabled) { onClick?.invoke() },
      isEnabled = isEnabled,
      text = name,
      summary = summary,
      icon = icon,
      trailing = { enabled ->
        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = { newChecked ->
              if (newChecked) {
                hapticManager?.toggleOn()
              } else {
                hapticManager?.toggleOff()
              }
              onCheckedChanged(newChecked)
            },
        )
      },
  )
}

@Composable
internal fun ListPreferenceItem(
    modifier: Modifier = Modifier,
    preference: Preferences.ListPreference,
    onOpenDialog: () -> Unit,
) {
  val isEnabled = preference.isEnabled
  val title = preference.name
  val summary = preference.summary
  val icon = preference.icon

  PreferenceItem(
      modifier = modifier.clickable(enabled = isEnabled) { onOpenDialog() },
      isEnabled = isEnabled,
      text = title,
      summary = summary,
      icon = icon,
  )
}

@Composable
internal fun PreferenceDialog(
    modifier: Modifier = Modifier,
    preference: Preferences.ListPreference,
    onDismiss: () -> Unit
) {
  val title = preference.name
  val currentValue = preference.value
  val entries = preference.entries
  val onPreferenceSelected = preference.onPreferenceSelected

  val hapticManager = LocalHapticManager.current

  Dialog(
      properties = rememberDialogProperties(),
      onDismissRequest = onDismiss,
  ) {
    Surface(
        modifier = modifier.padding(MaterialTheme.keylines.content),
        elevation = DialogDefaults.Elevation,
        shape = MaterialTheme.shapes.medium,
    ) {
      LazyColumn {
        item(
            contentType = PreferenceContentTypes.DIALOG_TITLE,
        ) {
          PreferenceDialogTitle(
              modifier =
                  Modifier.fillMaxWidth().padding(horizontal = MaterialTheme.keylines.content),
              title = title,
          )
        }

        items(
            items = entries,
            key = { it.value },
            contentType = { PreferenceContentTypes.DIALOG_ITEM },
        ) { item ->
          PreferenceDialogItem(
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(
                          horizontal = MaterialTheme.keylines.content,
                          vertical = MaterialTheme.keylines.typography,
                      ),
              name = item.key,
              value = item.value,
              current = currentValue,
              onClick = { k, v ->
                hapticManager?.actionButtonPress()
                onPreferenceSelected(k, v)
                onDismiss()
              },
          )
        }

        item(
            contentType = PreferenceContentTypes.DIALOG_ACTION,
        ) {
          PreferenceDialogActions(
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(horizontal = MaterialTheme.keylines.content)
                      .padding(bottom = MaterialTheme.keylines.baseline),
              onDismiss = {
                hapticManager?.cancelButtonPress()
                onDismiss()
              },
          )
        }
      }
    }
  }
}

@Composable
private fun PreferenceDialogTitle(
    modifier: Modifier = Modifier,
    title: String,
) {
  Text(
      modifier = modifier.padding(MaterialTheme.keylines.content),
      text = title,
      style = MaterialTheme.typography.h6,
  )
}

@Composable
private fun PreferenceDialogItem(
    modifier: Modifier = Modifier,
    name: String,
    value: String,
    current: String,
    onClick: (name: String, value: String) -> Unit,
) {
  val isSelected =
      remember(
          value,
          current,
      ) {
        value == current
      }

  val handleClick by rememberUpdatedState {
    if (!isSelected) {
      onClick(name, value)
    }
  }

  Row(
      modifier =
          Modifier.selectable(
                  selected = isSelected,
                  onClick = { handleClick() },
              )
              .then(modifier),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    RadioButton(
        modifier = Modifier.padding(end = MaterialTheme.keylines.baseline),
        selected = isSelected,
        onClick = { handleClick() },
    )
    Text(
        text = name,
        style = MaterialTheme.typography.body1,
    )
  }
}

@Composable
private fun PreferenceDialogActions(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
  Row(
      modifier = modifier,
  ) {
    Spacer(
        modifier = Modifier.weight(1F),
    )

    TextButton(
        onClick = onDismiss,
    ) {
      Text(
          text = stringResource(R.string.close),
      )
    }
  }
}

@Composable
private fun PreferenceItem(
    modifier: Modifier = Modifier,
    isEnabled: Boolean,
    text: String,
    summary: String,
    icon: ImageVector?,
    trailing: (@Composable (isEnabled: Boolean) -> Unit)? = null,
    badge: (@Composable () -> Unit)? = null,
    customContent: (@Composable (isEnabled: Boolean) -> Unit)? = null,
) {
  val isGroupEnabled = LocalPreferenceEnabledStatus.current
  val enabled = isGroupEnabled && isEnabled

  PreferenceAlphaWrapper(
      isEnabled = enabled,
  ) {
    if (customContent == null) {
      DefaultPreferenceItem(
          modifier = modifier,
          enabled = enabled,
          text = text,
          summary = summary,
          icon = icon,
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
    modifier: Modifier = Modifier,
    enabled: Boolean,
    text: String,
    summary: String,
    icon: ImageVector?,
    trailing: (@Composable (isEnabled: Boolean) -> Unit)? = null,
    badge: (@Composable () -> Unit)? = null,
) {
  val textColor = LocalContentColor.current

  Row(
      modifier = modifier.padding(all = MaterialTheme.keylines.baseline),
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
            style =
                MaterialTheme.typography.body1.copy(
                    color =
                        textColor.copy(
                            alpha = if (enabled) ContentAlpha.high else ContentAlpha.medium,
                        ),
                ),
        )
        badge?.let { compose ->
          Box(
              modifier = Modifier.padding(start = MaterialTheme.keylines.baseline),
          ) {
            compose()
          }
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
              style =
                  MaterialTheme.typography.caption.copy(
                      color =
                          textColor.copy(
                              alpha = if (enabled) ContentAlpha.medium else ContentAlpha.disabled,
                          ),
                  ),
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
      ) {
        compose(enabled)
      }
    }
  }
}
