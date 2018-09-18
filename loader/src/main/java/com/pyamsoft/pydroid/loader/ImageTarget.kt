package com.pyamsoft.pydroid.loader

import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.CheckResult

interface ImageTarget<T : Any> {

  @CheckResult
  fun view(): View

  fun clear()

  fun setImage(image: T)

  fun setError(error: Drawable?)

  fun setPlaceholder(placeholder: Drawable?)
}
