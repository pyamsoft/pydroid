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

/** Allows Preferences to use VectorDrawables as icons on API < 21 */
package com.pyamsoft.pydroid.ui.preference

import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.UUID

/** A Preferences model */
public sealed class Preferences {

  /** Key for rendering */
  internal val renderKey: String = UUID.randomUUID().toString()

  /** Name */
  internal abstract val name: String

  /** Enabled */
  internal abstract val isEnabled: Boolean

  /** Represents a single Preference item */
  public abstract class Item protected constructor() : Preferences() {

    /** Summary */
    internal abstract val summary: String

    /** Icon */
    internal abstract val icon: ImageVector?
  }

  /** Represents a simple Preference item */
  internal data class SimplePreference
  internal constructor(
      override val name: String,
      override val isEnabled: Boolean,
      override val summary: String,
      override val icon: ImageVector?,
      internal val onClick: (() -> Unit)?,
  ) : Item()

  /** Represents a Custom Preference item */
  internal data class CustomPreference
  internal constructor(
      override val name: String = "",
      override val isEnabled: Boolean = false,
      override val summary: String = "",
      override val icon: ImageVector? = null,
      internal val content: @Composable (isEnabled: Boolean) -> Unit,
  ) : Item()

  /** Represents a Ad Preference item */
  internal data class AdPreference
  internal constructor(
      override val name: String,
      override val isEnabled: Boolean,
      override val summary: String,
      override val icon: ImageVector?,
      internal val onClick: (() -> Unit)?,
  ) : Item()

  /** Represents a In-App Purchase Preference item */
  internal data class InAppPreference
  internal constructor(
      override val name: String,
      override val isEnabled: Boolean,
      override val summary: String,
      override val icon: ImageVector?,
      internal val onClick: (() -> Unit)?,
  ) : Item()

  /** Represents a List Preference item */
  internal data class ListPreference
  internal constructor(
      override val name: String,
      override val isEnabled: Boolean,
      override val summary: String,
      override val icon: ImageVector?,
      internal val value: String,
      internal val entries: Map<String, String>,
      internal val onPreferenceSelected: (key: String, value: String) -> Unit,
  ) : Item()

  /** Represents a CheckBox Preference item */
  internal data class CheckBoxPreference
  internal constructor(
      override val name: String,
      override val isEnabled: Boolean,
      override val summary: String,
      override val icon: ImageVector?,
      internal val checked: Boolean,
      internal val onCheckedChanged: (checked: Boolean) -> Unit,
  ) : Item()

  /** Represents a Switch Preference item */
  internal data class SwitchPreference
  internal constructor(
      override val name: String,
      override val isEnabled: Boolean,
      override val summary: String,
      override val icon: ImageVector?,
      internal val checked: Boolean,
      internal val onCheckedChanged: (checked: Boolean) -> Unit,
  ) : Item()

  /** Represents a group of Preferences */
  public data class Group
  internal constructor(
      override val name: String,
      override val isEnabled: Boolean,
      internal val preferences: List<Item>
  ) : Preferences()
}

/** Create a new Preference.Group */
@CheckResult
@JvmOverloads
public fun preferenceGroup(
    name: String,
    isEnabled: Boolean = true,
    preferences: List<Preferences.Item>,
): Preferences.Group {
  return Preferences.Group(
      name = name,
      isEnabled = isEnabled,
      preferences = preferences,
  )
}

/** Create a new Preference.AdPreference */
@CheckResult
public fun customPreference(
    content: @Composable (isEnabled: Boolean) -> Unit,
): Preferences.Item {
  return Preferences.CustomPreference(
      content = content,
  )
}

/** Create a new Preference.SimplePreference */
@CheckResult
@JvmOverloads
public fun preference(
    name: String,
    isEnabled: Boolean = true,
    summary: String = "",
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
): Preferences.Item {
  return Preferences.SimplePreference(
      name = name,
      isEnabled = isEnabled,
      summary = summary,
      icon = icon,
      onClick = onClick,
  )
}

/** Create a new Preference.AdPreference */
@CheckResult
@JvmOverloads
public fun adPreference(
    name: String,
    isEnabled: Boolean = true,
    summary: String = "",
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
): Preferences.Item {
  return Preferences.AdPreference(
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
    name: String,
    isEnabled: Boolean = true,
    summary: String = "",
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
): Preferences.Item {
  return Preferences.InAppPreference(
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
    name: String,
    value: String,
    entries: Map<String, String>,
    isEnabled: Boolean = true,
    summary: String = "",
    icon: ImageVector? = null,
    onPreferenceSelected: (key: String, value: String) -> Unit,
): Preferences.Item {
  return Preferences.ListPreference(
      name = name,
      isEnabled = isEnabled,
      summary = summary,
      value = value,
      entries = entries,
      icon = icon,
      onPreferenceSelected = onPreferenceSelected,
  )
}

/** Create a new Preference.CheckBoxPreference */
@CheckResult
@JvmOverloads
public fun checkBoxPreference(
    name: String,
    isEnabled: Boolean = true,
    summary: String = "",
    icon: ImageVector? = null,
    checked: Boolean,
    onCheckedChanged: (checked: Boolean) -> Unit,
): Preferences.Item {
  return Preferences.CheckBoxPreference(
      name = name,
      isEnabled = isEnabled,
      summary = summary,
      icon = icon,
      checked = checked,
      onCheckedChanged = onCheckedChanged,
  )
}

/** Create a new Preference.SwitchPreference */
@CheckResult
@JvmOverloads
public fun switchPreference(
    name: String,
    isEnabled: Boolean = true,
    summary: String = "",
    icon: ImageVector? = null,
    checked: Boolean,
    onCheckedChanged: (checked: Boolean) -> Unit,
): Preferences.Item {
  return Preferences.SwitchPreference(
      name = name,
      isEnabled = isEnabled,
      summary = summary,
      icon = icon,
      checked = checked,
      onCheckedChanged = onCheckedChanged,
  )
}
