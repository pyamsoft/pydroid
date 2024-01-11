/*
 * Copyright 2024 pyamsoft
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

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.core.requireNotNull

/** The application is in debug mode if the DEBUGGABLE flag is set */
@CheckResult
public fun Context.isDebugMode(): Boolean {
  val flags = this.applicationContext.applicationInfo.flags
  return flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
}

private var applicationName: String? = null

@CheckResult
private fun resolveApplicationName(): (Context) -> String {

  return fun(context: Context): String {
    if (applicationName == null) {
      applicationName = context.applicationInfo.loadLabel(context.packageManager).toString()
    }

    return applicationName.requireNotNull()
  }
}

private val applicationNameResolver = resolveApplicationName()

/** Load the name of the Application from the package manager */
public val Context.applicationDisplayName: String
  @CheckResult
  get() {
    return applicationNameResolver(this.applicationContext)
  }
