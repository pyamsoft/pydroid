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

package com.pyamsoft.pydroid.theme

import android.annotation.SuppressLint
import android.app.Activity
import androidx.annotation.AttrRes
import androidx.annotation.CheckResult

/** Pulls an attribute from the current Activity theme */
@CheckResult
public fun Activity.attributeFromCurrentTheme(@AttrRes attr: Int): Int {
  val attrs = this.attributesFromCurrentTheme(attr)
  return attrs.getOrElse(0) { 0 }
}

/** Pulls an attribute from the current Activity theme */
@CheckResult
@SuppressLint("ResourceType")
public fun Activity.attributesFromCurrentTheme(@AttrRes vararg attrs: Int): IntArray {
  return IntArray(attrs.size).also { styled ->
    val arr = this.obtainStyledAttributes(attrs)
    try {
      for (index in attrs.indices) {
        styled[index] = arr.getResourceId(index, 0)
      }
    } finally {
      arr.recycle()
    }
  }
}
