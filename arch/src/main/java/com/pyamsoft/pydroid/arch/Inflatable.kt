package com.pyamsoft.pydroid.arch

interface Inflatable<S : UiViewState> {

    fun inflate(savedInstanceState: UiBundleReader)

    fun teardown()
}
