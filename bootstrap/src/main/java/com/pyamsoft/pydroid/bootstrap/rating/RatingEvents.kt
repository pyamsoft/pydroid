package com.pyamsoft.pydroid.bootstrap.rating

interface RatingEvents {

  object ShowEvent

  data class ShowErrorEvent(val error: Throwable)

  data class SaveErrorEvent(val error: Throwable)

}