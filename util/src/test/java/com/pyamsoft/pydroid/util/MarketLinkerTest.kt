/*
 * Copyright 2026 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
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

import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(minSdk = 26)
public class MarketLinkerTest {

  @Test
  public fun getStorePageLink_noDevSuffix_unchanged() {
    val context = RuntimeEnvironment.getApplication()
    assertEquals(
        "market://details?id=com.example.app",
        MarketLinker.getStorePageLink(context, "com.example.app"),
    )
  }

  @Test
  public fun getStorePageLink_devSuffix_stripsOnlyTrailingSuffix() {
    val context = RuntimeEnvironment.getApplication()
    assertEquals(
        "market://details?id=com.example.app",
        MarketLinker.getStorePageLink(context, "com.example.app.dev"),
    )
  }

  @Test
  public fun getStorePageLink_devInMiddleOfName_notStripped() {
    val context = RuntimeEnvironment.getApplication()
    assertEquals(
        "market://details?id=com.example.dev.app",
        MarketLinker.getStorePageLink(context, "com.example.dev.app.dev"),
    )
  }

  @Test
  public fun getStorePageLink_defaultsToContextPackageName() {
    val context = RuntimeEnvironment.getApplication()
    assertEquals(
        MarketLinker.getStorePageLink(context, context.packageName),
        MarketLinker.getStorePageLink(context),
    )
  }

  @Test
  public fun getDeveloperPageLink_isConstant() {
    assertEquals(
        "https://play.google.com/store/apps/dev?id=8240502725675466993",
        MarketLinker.getDeveloperPageLink(),
    )
  }
}
