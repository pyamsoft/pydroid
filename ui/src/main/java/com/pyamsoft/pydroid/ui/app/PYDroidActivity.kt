/*
 * Copyright 2023 pyamsoft
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

package com.pyamsoft.pydroid.ui.app

import androidx.annotation.CheckResult
import androidx.appcompat.app.AppCompatActivity
import com.pyamsoft.pydroid.ui.changelog.ChangeLogProvider
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.ui.internal.pydroid.PYDroidActivityDelegateInternal

@CheckResult
private fun createPYDroidDelegate(
    activity: AppCompatActivity,
    provider: ChangeLogProvider,
    options: PYDroidActivityOptions,
): PYDroidActivityDelegateInternal {
  val component =
      ObjectGraph.ApplicationScope.retrieve(activity.application)
          .injector()
          .plusApp()
          .create(options)
  return PYDroidActivityDelegateInternal(component, provider, activity)
}

/**
 * Install PYDroid into an Activity
 *
 * Returns a delegate that can optionally be saved or used in the calling Activity level to handle
 * common functions like checking for updates or showing an in-app review dialog
 */
@JvmOverloads
public fun AppCompatActivity.installPYDroid(
    provider: ChangeLogProvider,
    options: PYDroidActivityOptions = PYDroidActivityOptions(),
): PYDroidActivityDelegate {
  val self = this
  val internals = createPYDroidDelegate(self, provider, options)
  ObjectGraph.ActivityScope.install(self, internals)
  return internals
}
