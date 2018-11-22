package com.pyamsoft.pydroid.ui.about

import com.pyamsoft.pydroid.ui.app.BaseView

interface LicenseView : BaseView {

  fun loadView(onDismiss: () -> Unit)

  fun onMenuItemClick(onClick: () -> Unit)

}