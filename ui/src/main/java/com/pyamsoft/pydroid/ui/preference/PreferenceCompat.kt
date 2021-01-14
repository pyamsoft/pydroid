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

/**
 * Allows Preferences to use VectorDrawables as icons on API < 21
 */
package com.pyamsoft.pydroid.ui.preference

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import androidx.preference.R

/**
 * Preference that uses vector drawables
 */
public open class PreferenceCompat : Preference {

    /**
     * Construct
     */
    public constructor(context: Context) : this(context, null)

    /**
     * Construct
     */
    public constructor(
        context: Context,
        attrs: AttributeSet?
    ) : this(
        context, attrs, context.getStyledAttr(
            R.attr.preferenceStyle,
            android.R.attr.preferenceStyle
        )
    )

    /**
     * Construct
     */
    public constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : this(context, attrs, defStyleAttr, 0)

    /**
     * Construct
     */
    public constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        loadIconCompat(attrs)
    }
}
