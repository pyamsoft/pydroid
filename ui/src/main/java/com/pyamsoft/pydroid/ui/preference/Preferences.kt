/**
 * Allows Preferences to use VectorDrawables as icons on API < 21
 */
package com.pyamsoft.pydroid.ui.preference

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.CheckResult
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.withStyledAttributes
import androidx.preference.Preference
import com.pyamsoft.pydroid.ui.R

fun Preference.loadIconCompat(attrs: AttributeSet?) {
  if (attrs != null) {
    context.withStyledAttributes(attrs, R.styleable.PreferenceCompat) {
      val iconResId = getResourceId(R.styleable.PreferenceCompat_iconCompat, 0)
      if (iconResId != 0) {
        val icon = AppCompatResources.getDrawable(context, iconResId)
        setIcon(icon)
      }
    }
  }
}

@CheckResult
@AttrRes
fun Context.getStyledAttr(
  @AttrRes attr: Int,
  @AttrRes fallbackAttr: Int
): Int {
  val value = TypedValue()
  theme.resolveAttribute(attr, value, true)
  if (value.resourceId != 0) {
    return attr
  }
  return fallbackAttr
}
