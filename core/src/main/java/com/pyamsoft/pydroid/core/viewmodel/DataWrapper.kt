package com.pyamsoft.pydroid.core.viewmodel

import com.pyamsoft.pydroid.core.bus.Listener
import com.pyamsoft.pydroid.core.bus.RxBus
import io.reactivex.Observable

// The common set of activity events
sealed class DataWrapper<T : Any> {
  internal class Loading<T : Any> internal constructor(val forced: Boolean) : DataWrapper<T>()
  internal data class Success<T : Any> internal constructor(val data: T) : DataWrapper<T>()
  internal data class Error<T : Any> internal constructor(val error: Throwable) : DataWrapper<T>()
  internal class Complete<T : Any> internal constructor() : DataWrapper<T>()

  fun onLoading(func: (Boolean) -> Unit) {
    if (this is Loading) {
      func(this.forced)
    }
  }

  fun onSuccess(func: (T) -> Unit) {
    if (this is Success) {
      func(this.data)
    }
  }

  fun onError(func: (Throwable) -> Unit) {
    if (this is Error) {
      func(this.error)
    }
  }

  fun onComplete(func: () -> Unit) {
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
    publish(DataWrapper.Loading(forced))
  }

  fun publishSuccess(data: T) {
    publish(DataWrapper.Success(data))
  }

  fun publishError(error: Throwable) {
    publish(DataWrapper.Error(error))
  }

  fun publishComplete() {
    // Notify active watchers that we are complete
    publish(DataWrapper.Complete())
  }

}

