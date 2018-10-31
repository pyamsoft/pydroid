package com.pyamsoft.pydroid.ui.util

import android.view.View
import com.pyamsoft.pydroid.util.hyperlink

object MarketLinker {

  private const val BASE_MARKET = "market://details?id="
  private const val DEVELOPER_PAGE = "https://play.google.com/store/apps/dev?id=5257476342110165153"

  fun linkToMarketPage(
    packageName: String,
    view: View
  ) {
    "$BASE_MARKET$packageName".hyperlink(view.context)
        .navigate(view)
  }

  fun linkToDeveloperPage(view: View) {
    DEVELOPER_PAGE.hyperlink(view.context)
        .navigate(view)
  }

}
