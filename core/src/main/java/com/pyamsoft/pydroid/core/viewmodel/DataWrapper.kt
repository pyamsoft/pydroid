package com.pyamsoft.pydroid.core.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.pyamsoft.pydroid.core.viewmodel.DataWrapper.Empty

// The common set of activity events
sealed class DataWrapper<T : Any> {
  internal class Empty<T : Any> internal constructor() : DataWrapper<T>()
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

class LiveDataWrapper<T : Any> : MutableLiveData<DataWrapper<T>>() {

  inline fun observe(
    owner: LifecycleOwner,
    crossinline func: (DataWrapper<T>) -> Unit
  ) {
    observe(owner, Observer {
      if (it !is Empty) {
        func(it)
      }
    })
  }

  fun publishLoading(forced: Boolean) {
    super.setValue(DataWrapper.Loading(forced))
  }

  fun publishSuccess(data: T) {
    super.setValue(DataWrapper.Success(data))
  }

  fun publishError(error: Throwable) {
    super.setValue(DataWrapper.Error(error))
  }

  fun publishComplete() {
    // Notify active watchers that we are complete
    super.setValue(DataWrapper.Complete())

    // Publish an Empty event to reset the completed state
    // The next new observer will not instantly receive a Completed event
    super.setValue(DataWrapper.Empty())
  }

  override fun setValue(value: DataWrapper<T>) {
    throw RuntimeException("Do not access setValue directly, use publish methods")
  }

}

