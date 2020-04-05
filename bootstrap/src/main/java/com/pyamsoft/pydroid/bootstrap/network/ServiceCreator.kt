package com.pyamsoft.pydroid.bootstrap.network

import androidx.annotation.CheckResult

interface ServiceCreator {

    @CheckResult
    fun <S : Any> createService(serviceClass: Class<S>): S
}
