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

package com.pyamsoft.pydroid.ui.internal.pydroid

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.util.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.util.doOnDestroy

internal object ObjectGraph {

  internal object ApplicationScope {

    private val trackingMap = mutableMapOf<Application, PYDroid>()

    fun install(
        application: Application,
        internals: PYDroid,
    ) {
      trackingMap[application] = internals
      Logger.d { "Track PYDroid.Application install: $application $internals" }
    }

    @CheckResult
    fun retrieve(application: Application): PYDroid {
      return trackingMap[application].requireNotNull {
        "Could not find PYDroid internals for Application: $application"
      }
    }
  }

  internal object ActivityScope {

    private val trackingMap = mutableMapOf<ComponentActivity, PYDroidActivityDelegateInternal>()

    fun install(
        activity: ComponentActivity,
        internals: PYDroidActivityDelegateInternal,
    ) {
      trackingMap[activity] = internals
      Logger.d { "Track PYDroid.Activity install: $activity $internals" }

      activity.doOnDestroy {
        Logger.d { "Remove PYDroid.Activity internals on Destroy" }
        trackingMap.remove(activity)
      }
    }

    @CheckResult
    fun retrieve(activity: ComponentActivity): PYDroidActivityDelegateInternal {
      return trackingMap[activity].requireNotNull {
        "Could not find PYDroidActivity internals for Activity: $activity"
      }
    }
  }
}
