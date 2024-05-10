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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.app.rememberDialogProperties
import com.pyamsoft.pydroid.ui.defaults.ListItemDefaults
import com.pyamsoft.pydroid.ui.haptics.LocalHapticManager
import com.pyamsoft.pydroid.ui.internal.app.InAppBadge
import com.pyamsoft.pydroid.ui.preference.PreferenceDialogItemStyle.CHECKBOX
import com.pyamsoft.pydroid.ui.preference.PreferenceDialogItemStyle.RADIO

private enum class PreferenceContentTypes {
  DIALOG_CHECKBOXES,
  DIALOG_ITEM,
}

private enum class PreferenceDialogItemStyle {
  RADIO,
  CHECKBOX,
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
            modifier =
                Modifier.padding(
                    start = MaterialTheme.keylines.typography,
                    end = MaterialTheme.keylines.baseline,
                ),
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
            modifier =
                Modifier.padding(
                    start = MaterialTheme.keylines.typography,
                    end = MaterialTheme.keylines.baseline,
                ),
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
    Card(
        modifier = modifier.padding(MaterialTheme.keylines.content),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.elevatedCardElevation(),
        colors = CardDefaults.elevatedCardColors(),
    ) {
      PreferenceDialogTitle(
          modifier = Modifier.fillMaxWidth().padding(horizontal = MaterialTheme.keylines.content),
          title = title,
      )

      LazyColumn(
          modifier =
              Modifier.weight(
                  weight = 1F,
                  fill = false,
              ),
      ) {
        items(
            items = entries,
            key = { it.value },
            contentType = { PreferenceContentTypes.DIALOG_ITEM },
        ) { item ->
          val isSelected = remember(item, currentValue) { item.value == currentValue }
          PreferenceDialogItem(
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(
                          horizontal = MaterialTheme.keylines.content,
                          vertical = MaterialTheme.keylines.typography,
                      ),
              name = item.key,
              isSelected = isSelected,
              style = RADIO,
              onClick = {
                hapticManager?.actionButtonPress()
                onPreferenceSelected(item.key, item.value)
                onDismiss()
              },
          )
        }

        preference.checkboxes?.let { checkboxes ->
          items(
              items = checkboxes,
              key = { it.value },
              contentType = { PreferenceContentTypes.DIALOG_CHECKBOXES },
          ) { item ->
            val isSelected = remember(item) { item.value }
            PreferenceDialogItem(
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(
                            horizontal = MaterialTheme.keylines.content,
                            vertical = MaterialTheme.keylines.typography,
                        ),
                name = item.key,
                isSelected = isSelected,
                style = CHECKBOX,
                onClick = {
                  hapticManager?.actionButtonPress()
                  onPreferenceSelected(item.key, item.value.not().toString())
                },
            )
          }
        }
      }

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

@Composable
private fun PreferenceDialogTitle(
    modifier: Modifier = Modifier,
    title: String,
) {
  Text(
      modifier = modifier.padding(MaterialTheme.keylines.content),
      text = title,
      style = MaterialTheme.typography.titleLarge,
  )
}

@Composable
private fun PreferenceDialogItem(
    modifier: Modifier = Modifier,
    name: String,
    isSelected: Boolean,
    style: PreferenceDialogItemStyle,
    onClick: () -> Unit,
) {
  val handleClick by rememberUpdatedState(onClick)

  Row(
      modifier =
          Modifier.selectable(
                  selected = isSelected,
                  onClick = { handleClick() },
              )
              .then(modifier),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    when (style) {
      RADIO -> {
        RadioButton(
            modifier = Modifier.padding(end = MaterialTheme.keylines.baseline),
            selected = isSelected,
            onClick = { handleClick() },
        )
      }
      CHECKBOX -> {
        Checkbox(
            modifier = Modifier.padding(end = MaterialTheme.keylines.baseline),
            checked = isSelected,
            onCheckedChange = { handleClick() },
        )
      }
    }
    Text(
        text = name,
        style = MaterialTheme.typography.bodyLarge,
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
          text = stringResource(android.R.string.cancel),
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
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = textColor,
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
                MaterialTheme.typography.bodyLarge.copy(
                    color = textColor,
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
                  MaterialTheme.typography.bodyMedium.copy(
                      color = textColor,
                  ),
          )
        }
      }
    }

    trailing?.also { compose ->
      Box(
          modifier =
              Modifier.padding(
                      horizontal = MaterialTheme.keylines.baseline,
                  )
                  .size(ListItemDefaults.LeadingSize),
          contentAlignment = Alignment.Center,
      ) {
        compose(enabled)
      }
    }
  }
}
