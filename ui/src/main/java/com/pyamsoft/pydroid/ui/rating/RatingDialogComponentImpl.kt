package com.pyamsoft.pydroid.ui.rating

import android.text.SpannedString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.loader.LoaderModule

internal class RatingDialogComponentImpl internal constructor(
  private val ratingModule: RatingModule,
  private val loaderModule: LoaderModule,
  inflater: LayoutInflater,
  container: ViewGroup?,
  owner: LifecycleOwner,
  changeLogIcon: Int,
  changeLog: SpannedString
) : RatingDialogComponent {

  private val dialogView by lazy {
    RatingDialogViewImpl(
        inflater, container, loaderModule.provideImageLoader(),
        owner, changeLogIcon, changeLog
    )
  }

  override fun inject(dialog: RatingDialog) {
    dialog.rootView = dialogView
    dialog.viewModel = ratingModule.getViewModel()
    dialog.imageLoader = loaderModule.provideImageLoader()
  }

}