/*
 * Copyright 2022 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.internal.pydroid

import com.pyamsoft.pydroid.ui.changelog.ShowUpdateChangeLog
import com.pyamsoft.pydroid.ui.internal.billing.BillingUpsell
import com.pyamsoft.pydroid.ui.internal.datapolicy.DataPolicyDelegate
import com.pyamsoft.pydroid.ui.internal.rating.RatingDelegate
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckDelegate
import com.pyamsoft.pydroid.ui.version.VersionUpdateProgress
import com.pyamsoft.pydroid.ui.version.VersionUpgradeAvailable

internal data class PYDroidActivityComponents
internal constructor(
    internal val billingUpsell: BillingUpsell,
    internal val rating: RatingDelegate,
    internal val versionCheck: VersionCheckDelegate,
    internal val dataPolicy: DataPolicyDelegate,
    internal val versionUpgrader: VersionUpgradeAvailable,
    internal val versionUpdateProgress: VersionUpdateProgress,
    internal val showUpdateChangeLog: ShowUpdateChangeLog,
)
