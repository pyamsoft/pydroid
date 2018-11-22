package com.pyamsoft.pydroid.ui.app.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.pyamsoft.pydroid.ui.databinding.FragmentAppSettingsBinding

internal class AppSettingsViewImpl internal constructor(
  private val owner: LifecycleOwner,
  inflater: LayoutInflater,
  container: ViewGroup?
) : AppSettingsView, LifecycleObserver {

  private val binding = FragmentAppSettingsBinding.inflate(inflater, container, false)

  init {
    owner.lifecycle.addObserver(this)
  }

  @Suppress("unused")
  @OnLifecycleEvent(ON_DESTROY)
  internal fun destroy() {
    owner.lifecycle.removeObserver(this)

    binding.unbind()
  }

  override fun root(): View {
    return binding.root
  }

}