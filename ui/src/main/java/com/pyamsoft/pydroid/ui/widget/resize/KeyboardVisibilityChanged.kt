package com.pyamsoft.pydroid.ui.widget.resize

data class KeyboardVisibilityChanged internal constructor(
  val visible: Boolean,
  val contentHeight: Int,
  val contentHeightBeforeResize: Int
)