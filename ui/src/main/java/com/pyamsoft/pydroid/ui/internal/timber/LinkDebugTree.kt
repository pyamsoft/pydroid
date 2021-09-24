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

package com.pyamsoft.pydroid.ui.internal.timber

import timber.log.Timber

/**
 * A Tree which provides a link to the line the log is from
 *
 * https://proandroiddev.com/android-logging-on-steroids-clickable-logs-with-location-info-de1a5c16e86f
 */
internal class LinkDebugTree internal constructor() : Timber.DebugTree() {

  // Log the file and line so that you can click the link to the log
  override fun createStackElementTag(element: StackTraceElement): String {
    return element.run { "(${fileName}:${lineNumber}) ${methodName}()" }
  }
}
