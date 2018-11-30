package com.pyamsoft.pydroid.ui.about

import com.pyamsoft.pydroid.ui.app.BaseScreen

interface LicenseView : BaseScreen {

  fun loadView(onDismiss: () -> Unit)

  fun onMenuItemClick(onClick: () -> Unit)

}