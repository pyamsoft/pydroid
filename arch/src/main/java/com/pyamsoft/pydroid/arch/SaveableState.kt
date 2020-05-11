package com.pyamsoft.pydroid.arch

internal interface SaveableState {

    fun saveState(outState: UiBundleWriter)
}
