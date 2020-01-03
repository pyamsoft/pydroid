package com.pyamsoft.pydroid.arch

import android.os.Bundle

interface Inflatable<S : UiViewState> {

    fun inflate(savedInstanceState: Bundle?)

    fun teardown()
}
