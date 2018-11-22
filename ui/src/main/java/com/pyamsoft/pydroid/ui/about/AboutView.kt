package com.pyamsoft.pydroid.ui.about

import android.os.Bundle
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.app.BaseView

interface AboutView : BaseView {

  fun saveInstanceState(outState: Bundle)

  fun onLoadBegin(forced: Boolean)

  fun onLoadSuccess(libraries: List<OssLibrary>)

  fun onLoadError(throwable: Throwable)

  fun onLoadComplete()
}