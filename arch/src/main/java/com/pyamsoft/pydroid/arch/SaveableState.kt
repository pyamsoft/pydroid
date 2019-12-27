package com.pyamsoft.pydroid.arch

import android.os.Bundle

interface SaveableState {

    fun saveState(outState: Bundle)
}