/*
 * Copyright 2021 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.util

import android.content.Context
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.core.ResultWrapper

/** Links to play store market pages */
public object MarketLinker {

  private const val MARKET_URL = "market://details?id="
  private const val DEV_PAGE_URL = "https://play.google.com/store/apps/dev?id=8240502725675466993"

  /** Links to the market page for a specific app */
  @JvmStatic
  @CheckResult
  @JvmOverloads
  public fun linkToMarketPage(
      context: Context,
      packageName: String = context.packageName,
  ): ResultWrapper<Unit> {
    val targetName =
        if (packageName.endsWith(".dev")) {
          packageName.substringBefore(".dev")
        } else {
          packageName
        }

    return "$MARKET_URL$targetName".hyperlink(context).navigate()
  }

  /** Links to the pyamsoft developer page */
  @JvmStatic
  @CheckResult
  public fun linkToDeveloperPage(context: Context): ResultWrapper<Unit> {
    return DEV_PAGE_URL.hyperlink(context).navigate()
  }
}
