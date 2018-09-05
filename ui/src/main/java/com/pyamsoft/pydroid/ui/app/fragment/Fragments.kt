package com.pyamsoft.pydroid.ui.app.fragment

import android.os.Bundle
import android.view.View
import androidx.annotation.CheckResult
import androidx.fragment.app.Fragment
import com.pyamsoft.pydroid.ui.app.activity.ToolbarActivity

val Fragment.toolbarActivity: ToolbarActivity?
  @get:CheckResult get() {
    val a = activity
    if (a is ToolbarActivity) {
      return a
    } else {
      return null
    }
  }

@CheckResult
fun Fragment.requireToolbarActivity(): ToolbarActivity {
  return requireNotNull(toolbarActivity) { "ToolbarActivity is required and cannot be null." }
}

@CheckResult
fun Fragment.requireView(): View {
  return checkNotNull(view) { "View is required and cannot be null." }
}

@CheckResult
fun Fragment.requireArguments(): Bundle {
  return checkNotNull(arguments) { "Arguments are required and cannot be null." }
}
