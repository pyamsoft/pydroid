package com.pyamsoft.pydroid.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.about.AboutModule
import com.pyamsoft.pydroid.loader.ImageLoader

internal class AboutComponentImpl(
  private val aboutModule: AboutModule,
  private val imageLoader: ImageLoader,
  private val owner: LifecycleOwner,
  private val activity: FragmentActivity,
  private val inflater: LayoutInflater,
  private val container: ViewGroup?,
  savedInstanceState: Bundle?
) : AboutComponent {

  private val aboutView by lazy {
    AboutViewImpl(inflater, container, savedInstanceState, activity, owner)
  }

  override fun inject(fragment: AboutFragment) {
    fragment.rootView = aboutView
    fragment.viewModel = aboutModule.getViewModel()
  }

}
