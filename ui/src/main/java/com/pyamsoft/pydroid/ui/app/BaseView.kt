package com.pyamsoft.pydroid.ui.app

import android.view.View
import androidx.annotation.CheckResult

interface BaseView {

  @CheckResult
  fun root(): View

}