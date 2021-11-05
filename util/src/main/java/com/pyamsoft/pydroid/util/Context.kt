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

package com.pyamsoft.pydroid.util

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.annotation.CheckResult

/** The application is in debug mode if the DEBUGGABLE flag is set */
@CheckResult
public fun Context.isDebugMode(): Boolean {
  val flags = this.applicationContext.applicationInfo.flags
  return flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
}

@CheckResult
private fun nameResolver(context: Context): String {
  val appContext = context.applicationContext
  return appContext.applicationInfo.loadLabel(appContext.packageManager).toString()
}

@CheckResult
private fun resolveApplicationName(): (Context) -> String {
  return fun(context: Context): String {
    var applicationName: String? = null

    if (applicationName == null) {
      applicationName = nameResolver(context)
    }

    return applicationName
  }
}

private val applicationNameResolver = resolveApplicationName()

/** Load the name of the Application from the package manager */
public val Context.applicationDisplayName: String
  @get:CheckResult
  get() {
    return applicationNameResolver(this.applicationContext)
  }
