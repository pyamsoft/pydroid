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

package com.pyamsoft.pydroid.protection

import androidx.annotation.CheckResult
import androidx.appcompat.app.AppCompatActivity
import com.pyamsoft.pydroid.protection.internal.PirateProtection

/**
 * Protect yourself from pirates and other general security
 *
 * This is just to make sure that the APK youu are using in a release mode is built and verified by
 * official pyamsoft distribution sources (the Play Store)
 */
public interface Protection {

  /** Defend this application */
  public fun defend(activity: AppCompatActivity)

  public companion object {

    /** Creates a new protection module */
    @JvmStatic
    @CheckResult
    public fun create(licenseKey: String): Protection {
      return PirateProtection(licenseKey)
    }
  }
}
