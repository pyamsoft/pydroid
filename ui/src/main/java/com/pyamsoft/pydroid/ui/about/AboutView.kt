package com.pyamsoft.pydroid.ui.about

import android.os.Bundle
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.app.BaseScreen

interface AboutView : BaseScreen {

  fun saveInstanceState(outState: Bundle)

  fun onLoadBegin(forced: Boolean)

  fun onLoadSuccess(libraries: List<OssLibrary>)

  fun onLoadError(throwable: Throwable)

  fun onLoadComplete()
}