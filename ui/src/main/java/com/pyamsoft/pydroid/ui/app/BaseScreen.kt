package com.pyamsoft.pydroid.ui.app

import android.view.View
import androidx.annotation.CheckResult

interface BaseScreen : BaseView {

  @CheckResult
  fun root(): View

}