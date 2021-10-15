package com.pyamsoft.pydroid.ui.internal.app

import com.pyamsoft.pydroid.arch.UiViewState

internal interface AppViewState : UiViewState {
  val icon: Int
  val name: String
}
