package com.pyamsoft.pydroid.ui.about

import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.about.AboutLibrariesModule
import com.pyamsoft.pydroid.loader.LoaderModule

internal class AboutComponentImpl(
  private val owner: LifecycleOwner,
  private val aboutLibrariesModule: AboutLibrariesModule,
  private val loaderModule: LoaderModule
) : AboutComponent {

  override fun inject(fragment: AboutLibrariesFragment) {
    fragment.viewModel = aboutLibrariesModule.getViewModel(owner)
    fragment.imageLoader = loaderModule.provideImageLoader()
  }

}
