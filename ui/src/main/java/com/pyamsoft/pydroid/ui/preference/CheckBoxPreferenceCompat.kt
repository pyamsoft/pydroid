/**
 * Allows Preferences to use VectorDrawables as icons on API < 21
 */
package com.pyamsoft.pydroid.ui.preference

import android.content.Context
import android.util.AttributeSet
import androidx.preference.CheckBoxPreference
import com.pyamsoft.pydroid.ui.R

class CheckBoxPreferenceCompat : CheckBoxPreference {

  constructor(context: Context) : this(context, null)

  constructor(
    context: Context,
    attrs: AttributeSet?
  ) : this(
      context, attrs, context.getStyledAttr(
      R.attr.checkBoxPreferenceStyle,
      android.R.attr.checkBoxPreferenceStyle
  )
  )

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
