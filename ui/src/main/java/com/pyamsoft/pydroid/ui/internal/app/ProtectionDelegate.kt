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
 */

package com.pyamsoft.pydroid.ui.internal.app

import androidx.appcompat.app.AppCompatActivity
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.protection.Protection
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy

/** Handles Billing related work in an Activity */
internal class ProtectionDelegate(protection: Protection) {

  private var protection: Protection? = protection

  /** Connect to the billing service */
  fun connect(activity: AppCompatActivity) {
    protectApplication(activity)

    activity.doOnDestroy { protection = null }
  }

  /** Attempts to load and secure the application */
  private fun protectApplication(activity: AppCompatActivity) {
    Logger.d("Prepare application protection on create callback")
    activity.doOnCreate {
      Logger.d("Attempt protection")
      val protector = protection
      if (protector == null) {
        val msg = "Application Protection is not initialized!"
        val error = IllegalStateException(msg)
        Logger.e(error, msg)
        throw error
      } else {
        Logger.d("Application is created, protect")
        protector.defend(activity)
      }
    }
  }
}
