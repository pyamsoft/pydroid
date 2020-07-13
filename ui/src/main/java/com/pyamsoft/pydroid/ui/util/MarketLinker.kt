/*
 * Copyright 2020 Peter Kenji Yamanaka
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
 *
 */

package com.pyamsoft.pydroid.ui.util

import android.content.ActivityNotFoundException
import android.content.Context
import com.pyamsoft.pydroid.util.hyperlink

object MarketLinker {

    private const val BASE_MARKET = "market://details?id="
    private const val DEVELOPER_PAGE =
        "https://play.google.com/store/apps/dev?id=5257476342110165153"

    @JvmStatic
    @JvmOverloads
    fun linkToMarketPage(
        context: Context,
        packageName: String,
        onError: ((error: ActivityNotFoundException) -> Unit)? = null
    ): ActivityNotFoundException? {
        val targetName = if (packageName.endsWith(".dev")) {
            packageName.substringBefore(".dev")
        } else {
            packageName
        }

        val hyperlink = "$BASE_MARKET$targetName".hyperlink(context)
        return if (onError == null) {
            hyperlink.navigate()
        } else {
            hyperlink.navigate(onError)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun linkToDeveloperPage(
        context: Context,
        onError: ((error: ActivityNotFoundException) -> Unit)? = null
    ): ActivityNotFoundException? {
        val hyperlink = DEVELOPER_PAGE.hyperlink(context)
        return if (onError == null) {
            hyperlink.navigate()
        } else {
            hyperlink.navigate(onError)
        }
    }
}
