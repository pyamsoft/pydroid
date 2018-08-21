package com.pyamsoft.pydroid.core.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

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

class LiveDataWrapper<T : Any> : MutableLiveData<DataWrapper<T>>() {

  inline fun observe(
    owner: LifecycleOwner,
    crossinline func: (DataWrapper<T>) -> Unit
  ) {
    observe(owner, Observer { func(it) })
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
    super.setValue(DataWrapper.Complete())
  }

  override fun setValue(value: DataWrapper<T>) {
    throw RuntimeException("Do not access setValue directly, use publish methods")
  }

}

