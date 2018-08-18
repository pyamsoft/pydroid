package com.pyamsoft.pydroid.core.viewmodel

import com.pyamsoft.pydroid.core.bus.Listener

// Interface to avoid exposing the generic publish() function
interface ViewModelBus<T : Any> : Listener<DataWrapper<T>> {

  fun loading()

  fun success(data: T)

  fun error(error: Throwable)

  fun complete()

}
