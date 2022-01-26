/*
 * Copyright 2020 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.util

import android.annotation.SuppressLint
import android.app.Activity
import androidx.annotation.AttrRes
import androidx.annotation.CheckResult

/** Pulls an attribute from the current Activity theme */
@CheckResult
@SuppressLint("ResourceType")
@Deprecated("Use Compose-UI MaterialTheme")
public fun Activity.valueFromCurrentTheme(@AttrRes attr: Int): Int {
  return this.valuesFromCurrentTheme(attr)[0]
}

/** Pulls an attribute from the current Activity theme */
@CheckResult
@SuppressLint("ResourceType")
@Deprecated("Use Compose-UI MaterialTheme")
public fun Activity.valuesFromCurrentTheme(@AttrRes vararg attrs: Int): IntArray {
  val attributes = this.obtainStyledAttributes(attrs)
  val styled = IntArray(attrs.size)
  for (index in attrs.indices) {
    styled[index] = attributes.getResourceId(index, 0)
  }
  attributes.recycle()
  return styled
}
