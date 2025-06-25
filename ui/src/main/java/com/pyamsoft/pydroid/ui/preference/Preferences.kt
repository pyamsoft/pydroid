/*
 * Copyright 2025 pyamsoft
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

/** Allows Preferences to use VectorDrawables as icons on API < 21 */
package com.pyamsoft.pydroid.ui.preference

import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.vector.ImageVector

/** A Preferences model */
@Stable
public sealed class Preferences {

  /** Key for rendering */
  internal abstract val id: String

  /** Name */
  internal abstract val name: String

  /** Enabled */
  internal abstract val isEnabled: Boolean

  /** Represents a single Preference item */
  @Stable
  public abstract class Item protected constructor() : Preferences() {

    /** Summary */
    internal abstract val summary: String

    /** Icon */
    internal abstract val icon: ImageVector?
  }

  /** Represents a simple Preference item */
  @Stable
  @ConsistentCopyVisibility
  internal data class SimplePreference
  internal constructor(
      override val id: String,
      override val name: String,
      override val isEnabled: Boolean,
      override val summary: String,
      override val icon: ImageVector?,
      internal val onClick: (() -> Unit)?,
  ) : Item()

  /** Represents a Custom Preference item */
  @Stable
  @ConsistentCopyVisibility
  internal data class CustomPreference
  internal constructor(
      override val id: String,
      override val isEnabled: Boolean,
      override val name: String = "",
      override val summary: String = "",
      override val icon: ImageVector? = null,
      internal val content: @Composable (isEnabled: Boolean) -> Unit,
  ) : Item()

  /** Represents a In-App Purchase Preference item */
  @Stable
  @ConsistentCopyVisibility
  internal data class InAppPreference
  internal constructor(
      override val id: String,
      override val name: String,
      override val isEnabled: Boolean,
      override val summary: String,
      override val icon: ImageVector?,
      internal val onClick: (() -> Unit)?,
  ) : Item()

  /** Represents a List Preference item */
  @Stable
  @ConsistentCopyVisibility
  internal data class ListPreference
  internal constructor(
      override val id: String,
      override val name: String,
      override val isEnabled: Boolean,
      override val summary: String,
      override val icon: ImageVector?,
      internal val value: String,
      internal val entries: List<Map.Entry<String, String>>,
      internal val checkboxes: List<Map.Entry<String, Boolean>>?,
      internal val onPreferenceSelected: (key: String, value: String) -> Unit,
  ) : Item()

  /** Represents a CheckBox Preference item */
  @Stable
  @ConsistentCopyVisibility
  internal data class CheckBoxPreference
  internal constructor(
      override val id: String,
      override val name: String,
      override val isEnabled: Boolean,
      override val summary: String,
      override val icon: ImageVector?,
      internal val checked: Boolean,
      internal val onClick: (() -> Unit)?,
      internal val onCheckedChanged: (checked: Boolean) -> Unit,
  ) : Item()

  /** Represents a Switch Preference item */
  @Stable
  @ConsistentCopyVisibility
  internal data class SwitchPreference
  internal constructor(
      override val id: String,
      override val name: String,
      override val isEnabled: Boolean,
      override val summary: String,
      override val icon: ImageVector?,
      internal val checked: Boolean,
      internal val onClick: (() -> Unit)?,
      internal val onCheckedChanged: (checked: Boolean) -> Unit,
  ) : Item()

  /** Represents a group of Preferences */
  @Stable
  @ConsistentCopyVisibility
  public data class Group
  internal constructor(
      override val id: String,
      override val name: String,
      override val isEnabled: Boolean,
      internal val preferences: List<Item>
  ) : Preferences()
}

/** Create a new Preference.Group */
@CheckResult
@JvmOverloads
public fun preferenceGroup(
    id: String,
    name: String,
    isEnabled: Boolean = true,
    preferences: List<Preferences.Item>,
): Preferences.Group {
  return Preferences.Group(
      id = id,
      name = name,
      isEnabled = isEnabled,
      preferences = preferences,
  )
}

/** Create a new Preference.CustomPreference */
@CheckResult
public fun customPreference(
    id: String,
    isEnabled: Boolean = true,
    content: @Composable (isEnabled: Boolean) -> Unit,
): Preferences.Item {
  return Preferences.CustomPreference(
      id = id,
      isEnabled = isEnabled,
      content = content,
  )
}

/** Create a new Preference.SimplePreference */
@CheckResult
@JvmOverloads
public fun preference(
    id: String,
    name: String,
    isEnabled: Boolean = true,
    summary: String = "",
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
): Preferences.Item {
  return Preferences.SimplePreference(
      id = id,
      name = name,
      isEnabled = isEnabled,
      summary = summary,
      icon = icon,
      onClick = onClick,
  )
}

/** Create a new Preference.InAppPreference */
@CheckResult
@JvmOverloads
public fun inAppPreference(
    id: String,
    name: String,
    isEnabled: Boolean = true,
    summary: String = "",
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
): Preferences.Item {
  return Preferences.InAppPreference(
      id = id,
      name = name,
      isEnabled = isEnabled,
      summary = summary,
      icon = icon,
      onClick = onClick,
  )
}

/** Create a new Preference.ListPreference */
@CheckResult
@JvmOverloads
public fun listPreference(
    id: String,
    name: String,
    value: String,
    entries: Map<String, String>,
    isEnabled: Boolean = true,
    summary: String = "",
    icon: ImageVector? = null,
    checkboxes: Map<String, Boolean>? = null,
    onPreferenceSelected: (key: String, value: String) -> Unit,
): Preferences.Item {
  return Preferences.ListPreference(
      id = id,
      name = name,
      isEnabled = isEnabled,
      summary = summary,
      value = value,
      entries = entries.entries.toMutableStateList(),
      icon = icon,
      checkboxes = checkboxes?.entries?.toMutableStateList(),
      onPreferenceSelected = onPreferenceSelected,
  )
}

/** Create a new Preference.CheckBoxPreference */
@CheckResult
@JvmOverloads
public fun checkBoxPreference(
    id: String,
    name: String,
    isEnabled: Boolean = true,
    summary: String = "",
    icon: ImageVector? = null,
    checked: Boolean,
    onClick: (() -> Unit)? = null,
    onCheckedChanged: (checked: Boolean) -> Unit,
): Preferences.Item {
  return Preferences.CheckBoxPreference(
      id = id,
      name = name,
      isEnabled = isEnabled,
      summary = summary,
      icon = icon,
      checked = checked,
      onClick = onClick,
      onCheckedChanged = onCheckedChanged,
  )
}

/** Create a new Preference.SwitchPreference */
@CheckResult
@JvmOverloads
public fun switchPreference(
    id: String,
    name: String,
    isEnabled: Boolean = true,
    summary: String = "",
    icon: ImageVector? = null,
    checked: Boolean,
    onClick: (() -> Unit)? = null,
    onCheckedChanged: (checked: Boolean) -> Unit,
): Preferences.Item {
  return Preferences.SwitchPreference(
      id = id,
      name = name,
      isEnabled = isEnabled,
      summary = summary,
      icon = icon,
      checked = checked,
      onClick = onClick,
      onCheckedChanged = onCheckedChanged,
  )
}
