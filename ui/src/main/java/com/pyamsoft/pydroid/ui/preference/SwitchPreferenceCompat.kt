/**
 * Allows Preferences to use VectorDrawables as icons on API < 21
 */
package com.pyamsoft.pydroid.ui.preference

import android.content.Context
import android.util.AttributeSet
import androidx.preference.SwitchPreferenceCompat
import com.pyamsoft.pydroid.ui.R

class SwitchPreferenceCompat : SwitchPreferenceCompat {

  constructor(context: Context) : this(context, null)

  constructor(
    context: Context,
    attrs: AttributeSet?
  ) : this(context, attrs, R.attr.switchPreferenceCompatStyle)

  constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
  ) : this(context, attrs, defStyleAttr, 0)

  constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
  ) : super(context, attrs, defStyleAttr, defStyleRes) {
    loadIconCompat(attrs)
  }
}
