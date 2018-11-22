package com.pyamsoft.pydroid.bootstrap.version

interface VersionEvents {

  data class Begin(val forced: Boolean)

  data class UpdateFound(
    val currentVersion: Int,
    val newVersion: Int
  )

  data class UpdateError(val error: Throwable)

}