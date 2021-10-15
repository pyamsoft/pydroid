package com.pyamsoft.pydroid.ui.internal.app

import com.pyamsoft.pydroid.arch.UiViewState

internal interface AppState : UiViewState {
  val icon: Int
  val name: String
}
