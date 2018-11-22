package com.pyamsoft.pydroid.ui.about

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.loader.ImageLoader

internal class ViewLicenseComponentImpl internal constructor(
  inflater: LayoutInflater,
  container: ViewGroup?,
  owner: LifecycleOwner,
  imageLoader: ImageLoader,
  link: String,
  name: String
) : ViewLicenseComponent {

  private val licenseView by lazy {
    LicenseViewImpl(inflater, container, owner, imageLoader, link, name)
  }

  override fun inject(dialog: ViewLicenseDialog) {
    dialog.rootView = licenseView
  }

}
