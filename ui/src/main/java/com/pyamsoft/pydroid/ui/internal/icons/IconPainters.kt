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

package com.pyamsoft.pydroid.ui.internal.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.pyamsoft.pydroid.ui.R

/** Painters for Material Symbols */
internal object IconPainters {

  @Composable internal fun close(): Painter = painterResource(R.drawable.close_24px)

  @Composable internal fun themeMode(): Painter = painterResource(R.drawable.routine_24px)

  @Composable
  internal fun hapticFeedback(): Painter = painterResource(R.drawable.mobile_vibrate_24px)

  @Composable internal fun checkUpdates(): Painter = painterResource(R.drawable.download_24px)

  @Composable internal fun viewChangelog(): Painter = painterResource(R.drawable.whatshot_24px)

  @Composable internal fun rateApp(): Painter = painterResource(R.drawable.star_24px)

  @Composable internal fun tipJar(): Painter = painterResource(R.drawable.redeem_24px)

  @Composable
  internal fun tipJarDisabled(): Painter = painterResource(R.drawable.visibility_off_24px)

  @Composable internal fun debugMode(): Painter = painterResource(R.drawable.terminal_24px)

  @Composable internal fun resetAll(): Painter = painterResource(R.drawable.warning_24px)

  @Composable
  internal fun openSourceLicenses(): Painter = painterResource(R.drawable.library_books_24px)

  @Composable internal fun bugReport(): Painter = painterResource(R.drawable.bug_report_24px)

  @Composable internal fun viewSourceCode(): Painter = painterResource(R.drawable.code_24px)

  @Composable
  internal fun viewDataPolicyDisclosure(): Painter = painterResource(R.drawable.license_24px)

  @Composable internal fun viewPrivacyPolicy(): Painter = painterResource(R.drawable.policy_24px)

  @Composable internal fun viewTermsOfService(): Painter = painterResource(R.drawable.domain_24px)

  @Composable internal fun socialMedia(): Painter = painterResource(R.drawable.groups_24px)

  @Composable internal fun blog(): Painter = painterResource(R.drawable.article_shortcut_24px)
}
