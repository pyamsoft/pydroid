/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.app

import android.content.Context
import android.support.annotation.CallSuper
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceViewHolder
import android.util.AttributeSet

abstract class BaseBoundPreference : Preference {

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int,
      defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context,
      attrs, defStyleAttr)

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

  constructor(context: Context) : super(context)

  @CallSuper override fun onBindViewHolder(holder: PreferenceViewHolder) {
    super.onBindViewHolder(holder)
    onUnbindViewHolder(holder)
  }

  @CallSuper override fun onDetached() {
    super.onDetached()
    onUnbindViewHolder(null)
  }

  protected open fun onUnbindViewHolder(holder: PreferenceViewHolder?) {

  }
}
