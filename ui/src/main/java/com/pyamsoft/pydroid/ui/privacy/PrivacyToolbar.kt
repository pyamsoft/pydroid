/*
 * Copyright 2019 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.privacy

import androidx.appcompat.widget.Toolbar
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.privacy.PrivacyEvents.ViewPrivacyPolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun CoroutineScope.addPrivacy(
    toolbar: Toolbar,
    privacyPolicyUrl: String,
    termsConditionsUrl: String
) {
    toolbar.inflateMenu(R.menu.privacy_menu)

    toolbar.menu.apply {
        findItem(R.id.menu_id_privacy_policy)?.setOnMenuItemClickListener {
            launch {
                PrivacyEventBus.publish(ViewPrivacyPolicy(privacyPolicyUrl))
            }
            return@setOnMenuItemClickListener true
        }

        findItem(R.id.menu_id_t_c)?.setOnMenuItemClickListener {
            launch {
                PrivacyEventBus.publish(ViewPrivacyPolicy(termsConditionsUrl))
            }
            return@setOnMenuItemClickListener true
        }
    }
}

fun Toolbar.removePrivacy() {
    this.menu.apply {
        findItem(R.id.menu_id_privacy_policy)?.setOnMenuItemClickListener(null)
        findItem(R.id.menu_id_t_c)?.setOnMenuItemClickListener(null)
        removeItem(R.id.menu_id_privacy_policy)
        removeItem(R.id.menu_id_t_c)
    }
}
