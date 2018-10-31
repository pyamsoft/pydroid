package com.pyamsoft.pydroid.core.viewmodel

import com.pyamsoft.pydroid.core.viewmodel.DataWrapper.Complete
import com.pyamsoft.pydroid.core.viewmodel.DataWrapper.Error
import com.pyamsoft.pydroid.core.viewmodel.DataWrapper.Loading
import com.pyamsoft.pydroid.core.viewmodel.DataWrapper.Success
import com.pyamsoft.pydroid.core.bus.Listener
import com.pyamsoft.pydroid.core.bus.RxBus
import io.reactivex.Observable

// The common set of activity events
sealed class DataWrapper<T : Any> {
  data class Loading<T : Any> internal constructor(val forced: Boolean) : DataWrapper<T>()
  data class Success<T : Any> internal constructor(val data: T) : DataWrapper<T>()
  data class Error<T : Any> internal constructor(val error: Throwable) : DataWrapper<T>()
  class Complete<T : Any> internal constructor() : DataWrapper<T>()

  inline fun onLoading(func: (Boolean) -> Unit) {
    if (this is Loading) {
      func(this.forced)
    }
  }

  inline fun onSuccess(func: (T) -> Unit) {
    if (this is Success) {
      func(this.data)
    }
  }

  inline fun onError(func: (Throwable) -> Unit) {
    if (this is Error) {
      func(this.error)
    }
  }

  inline fun onComplete(func: () -> Unit) {
    if (this is Complete) {
      func()
    }
  }
}

class DataBus<T : Any> : Listener<DataWrapper<T>> {

  private val bus = RxBus.create<DataWrapper<T>>()

  override fun listen(): Observable<DataWrapper<T>> {
    return bus.listen()
  }

  private fun publish(event: DataWrapper<T>) {
    bus.publish(event)
  }

  fun publishLoading(forced: Boolean) {
    publish(Loading(forced))
  }

  fun publishSuccess(data: T) {
    publish(Success(data))
  }

  fun publishError(error: Throwable) {
    publish(Error(error))
  }

  fun publishComplete() {
    // Notify active watchers that we are complete
    publish(Complete())
  }

}

