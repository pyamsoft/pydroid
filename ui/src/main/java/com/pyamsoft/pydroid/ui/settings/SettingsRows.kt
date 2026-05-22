/*
 * Copyright 2026 pyamsoft
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

package com.pyamsoft.pydroid.ui.settings

import androidx.annotation.CheckResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.defaults.ListItemDefaults
import com.pyamsoft.pydroid.ui.haptics.LocalHapticManager
import com.pyamsoft.pydroid.ui.internal.icons.IconPainters

@CheckResult
private fun Modifier.maybeClickable(onClick: (() -> Unit)?): Modifier {
  val self = this
  if (onClick == null) {
    return self
  }

  return self.clickable(onClick = onClick)
}

/**
 * Render a settings row item using slots
 */
@Composable
public fun CustomSettingsRowItem(
  modifier: Modifier = Modifier,
  icon: (@Composable () -> Unit)?,
  title: @Composable () -> Unit,
  afterTitle: (@Composable () -> Unit)? = null,
  description: (@Composable () -> Unit)?,
  trailing: (@Composable () -> Unit)?,
  onClick: (() -> Unit)?,
) {
  Row(
    modifier = modifier
      .maybeClickable(onClick)
      .padding(all = MaterialTheme.keylines.baseline),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Start,
  ) {
    icon?.also { iconContent ->
      Box(
        modifier = Modifier.size(ListItemDefaults.LeadingSize),
        contentAlignment = Alignment.Center,
      ) {
        iconContent()
      }
    }

    Column(
      modifier = Modifier
        .padding(start = MaterialTheme.keylines.baseline)
        .weight(1F),
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.Start,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
      ) {
        title()
      }

      description?.also { descriptionContent ->
        Column(
          modifier =
            Modifier.padding(
              top = MaterialTheme.keylines.baseline,
            ),
        ) {
          descriptionContent()
        }
      }
    }

    trailing?.also { trailingContent ->
      Box(
        modifier =
          Modifier
            .padding(
              horizontal = MaterialTheme.keylines.baseline,
            )
            .size(ListItemDefaults.LeadingSize),
        contentAlignment = Alignment.Center,
      ) {
        trailingContent()
      }
    }
  }
}

/**
 * A basic settings item with icon, title, and optional description
 */
@Composable
public fun SimpleSettingsRowItem(
  modifier: Modifier = Modifier,
  icon: Painter,
  title: String,
  description: String = "",
  afterTitle: (@Composable () -> Unit)? = null,
  afterDescription: (@Composable () -> Unit)? = null,
  trailing: (@Composable () -> Unit)? = null,
  onClick: (() -> Unit)? = null,
) {
  val textColor = LocalContentColor.current

  CustomSettingsRowItem(
    modifier = modifier,
    icon = {
      Icon(
        painter = icon,
        contentDescription = title,
        tint = textColor,
      )
    },
    title = {
      Text(
        text = title,
        style =
          MaterialTheme.typography.bodyMedium.copy(
            color = textColor,
          ),
      )

      afterTitle?.invoke()
    },
    description = {
      Text(
        text = description,
        style =
          MaterialTheme.typography.bodySmall.copy(
            color = textColor,
          ),
      )

      afterDescription?.invoke()
    },
    trailing = trailing,
    onClick = onClick,
  )
}


/**
 * A basic settings item with icon, title, and optional description
 */
@Composable
public fun SettingsRowItem(
  modifier: Modifier = Modifier,
  icon: Painter,
  title: String,
  description: String = "",
  onClick: (() -> Unit)? = null,
) {
  SimpleSettingsRowItem(
    modifier = modifier,
    icon = icon,
    title = title,
    description = description,
    trailing = null,
    onClick = onClick,
  )
}

/**
 * A checkbox settings item with icon, title, and optional description
 */
@Composable
public fun CheckboxSettingsRowItem(
  modifier: Modifier = Modifier,
  icon: Painter,
  title: String,
  description: String = "",
  checked: Boolean,
  onChange: ((Boolean) -> Unit)? = null,
  onClick: (() -> Unit)? = null,
) {
  val hapticManager = LocalHapticManager.current

  SimpleSettingsRowItem(
    modifier = modifier,
    icon = icon,
    title = title,
    description = description,
    trailing = {
      Checkbox(
        modifier =
          Modifier.padding(
            start = MaterialTheme.keylines.typography,
            end = MaterialTheme.keylines.baseline,
          ),
        checked = checked,
        onCheckedChange = { newChecked ->
          if (newChecked) {
            hapticManager?.toggleOn()
          } else {
            hapticManager?.toggleOff()
          }
          onChange?.invoke(newChecked)
        },
      )
    },
    onClick = onClick,
  )
}

/**
 * A switch settings item with icon, title, and optional description
 */
@Composable
public fun SwitchSettingsRowItem(
  modifier: Modifier = Modifier,
  icon: Painter,
  title: String,
  description: String = "",
  checked: Boolean,
  onChange: ((Boolean) -> Unit)? = null,
  onClick: (() -> Unit)? = null,
) {
  val hapticManager = LocalHapticManager.current

  SimpleSettingsRowItem(
    modifier = modifier,
    icon = icon,
    title = title,
    description = description,
    trailing = {
      Switch(
        modifier =
          Modifier.padding(
            start = MaterialTheme.keylines.typography,
            end = MaterialTheme.keylines.baseline,
          ),
        checked = checked,
        onCheckedChange = { newChecked ->
          if (newChecked) {
            hapticManager?.toggleOn()
          } else {
            hapticManager?.toggleOff()
          }
          onChange?.invoke(newChecked)
        },
      )
    },
    onClick = onClick,
  )
}

/**
 * A basic settings item with icon, title, and optional description
 */
@Composable
public fun BadgeSettingsRowItem(
  modifier: Modifier = Modifier,
  icon: Painter,
  title: String,
  description: String = "",
  badge: @Composable () -> Unit,
  onClick: (() -> Unit)? = null,
) {
  SimpleSettingsRowItem(
    modifier = modifier,
    icon = icon,
    title = title,
    description = description,
    afterTitle = {
      Box(
        modifier = Modifier.padding(start = MaterialTheme.keylines.baseline),
      ) {
        badge()
      }
    },
    trailing = null,
    onClick = onClick,
  )
}

@Preview
@Composable
private fun PreviewSettingsRows() {
  Column(
    modifier = Modifier.background(color = Color.White),
  ) {
    SimpleSettingsRowItem(
      icon = IconPainters.close(),
      title = "DEFAULT TITLE",
      description = "DEFAULT DESCRIPTION"
    )

    CheckboxSettingsRowItem(
      icon = IconPainters.close(),
      title = "CHECKBOX TITLE",
      description = "CHECKBOX DESCRIPTION",
      checked = true,
      onChange = {},
    )

    SwitchSettingsRowItem(
      icon = IconPainters.close(),
      title = "SWITCH TITLE",
      description = "SWITCH DESCRIPTION",
      checked = false,
      onChange = {},
    )

    BadgeSettingsRowItem(
      icon = IconPainters.close(),
      title = "IN APP BADGE TITLE",
      description = "IN APP BADGE DESCRIPTION",
      badge = { InAppBadge() },
    )

    BadgeSettingsRowItem(
      icon = IconPainters.close(),
      title = "EXTERNAL LINK BADGE TITLE",
      description = "EXTERNAL LINK BADGE DESCRIPTION",
      badge = { ExternalLinkBadge() },
    )
  }
}

