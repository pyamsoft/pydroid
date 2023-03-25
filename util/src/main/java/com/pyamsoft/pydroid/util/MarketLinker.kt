/*
 * Copyright 2023 pyamsoft
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

/** Links to play store market pages */
public object MarketLinker {

  private const val MARKET_URL = "market://details?id="
  private const val DEV_PAGE_URL = "https://play.google.com/store/apps/dev?id=8240502725675466993"

  @JvmStatic
  @CheckResult
  @JvmOverloads
  public fun getStorePageLink(
      context: Context,
      packageName: String = context.packageName,
  ): String {
    val targetName =
        if (packageName.endsWith(".dev")) {
          packageName.substringBefore(".dev")
        } else {
          packageName
        }

    return "$MARKET_URL$targetName"
  }

  @JvmStatic
  @CheckResult
  public fun getDeveloperPageLink(): String {
    return DEV_PAGE_URL
  }
}
