package com.pyamsoft.pydroid.core.viewmodel

import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.bus.RxBus
import com.pyamsoft.pydroid.core.viewmodel.DataWrapper.Complete
import com.pyamsoft.pydroid.core.viewmodel.DataWrapper.Empty
import com.pyamsoft.pydroid.core.viewmodel.DataWrapper.Error
import com.pyamsoft.pydroid.core.viewmodel.DataWrapper.Loading
import com.pyamsoft.pydroid.core.viewmodel.DataWrapper.Success
import io.reactivex.Observable

internal class ViewModelBusImpl<T : Any> : ViewModelBus<T>, EventBus<DataWrapper<T>> {

  private val bus =
    RxBus.create<DataWrapper<T>>()

  override fun listen(): Observable<DataWrapper<T>> {
    return bus.listen()
        .onErrorReturnItem(Empty())
        .filter { it !is Empty }
  }

  override fun publish(event: DataWrapper<T>) {
    if (event !is Empty) {
      bus.publish(event)
    }
  }

  override fun loading() {
    publish(Loading())
  }

  override fun success(data: T) {
    publish(Success(data))
  }

  override fun error(error: Throwable) {
    publish(Error(error))
  }

  override fun complete() {
    publish(Complete())
  }

}
