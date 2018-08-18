package com.pyamsoft.pydroid.core.viewmodel

// The common set of activity events
// The unused T type is needed for smart casting
sealed class DataWrapper<T : Any> {
  // No-op event
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
