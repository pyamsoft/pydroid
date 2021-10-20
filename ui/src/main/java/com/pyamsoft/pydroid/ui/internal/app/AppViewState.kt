package com.pyamsoft.pydroid.ui.internal.app

import androidx.compose.runtime.Stable
import com.pyamsoft.pydroid.arch.UiViewState

@Stable
internal interface AppViewState : UiViewState {
  val icon: Int
  val name: String
}
