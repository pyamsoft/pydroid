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

package com.pyamsoft.pydroid.ui.rating

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.ui.internal.util.rememberPYDroidDelegate

/**
 * Attempts to show the Google In-App Rating Flow
 *
 * Due to internal implementation details, this call may not actually do anything, as it is up to
 * Google to decide when this dialog bit actually shows up. Quality API, Android.
 */
public fun showInAppRatingFlow(activity: ComponentActivity) {
  ObjectGraph.ActivityScope.retrieve(activity).loadInAppRating()
}

/**
 * Attempts to show the Google In-App Rating Flow
 *
 * Due to internal implementation details, this call may not actually do anything, as it is up to
 * Google to decide when this dialog bit actually shows up. Quality API, Android.
 */
@Composable
public fun LaunchShowInAppRatingFlow() {
  val pydroid = rememberPYDroidDelegate()
  LaunchedEffect(pydroid) { pydroid.loadInAppRating() }
}
