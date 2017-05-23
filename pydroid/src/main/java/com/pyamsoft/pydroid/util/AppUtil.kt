/*
 * Copyright 2017 Peter Kenji Yamanaka
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
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.support.annotation.CheckResult
import android.util.TypedValue
import timber.log.Timber

class AppUtil private constructor() {

  init {
    throw RuntimeException("No instances")
  }

  companion object {

    @JvmStatic @CheckResult fun getApplicationInfoIntent(packageName: String): Intent {
      val i = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
      i.addCategory(Intent.CATEGORY_DEFAULT)
      i.data = Uri.fromParts("package", packageName, null)
      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
      return i
    }

    @JvmStatic @CheckResult fun convertToDP(c: Context, px: Float): Float {
      val m = c.applicationContext.resources.displayMetrics
      val dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, m)
      Timber.d("Convert %f px to %f dp", px, dp)
      return dp
    }
  }
}
