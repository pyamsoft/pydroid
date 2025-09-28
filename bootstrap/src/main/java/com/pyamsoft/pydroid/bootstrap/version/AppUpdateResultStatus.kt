/*
 * Copyright 2025 pyamsoft
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

package com.pyamsoft.pydroid.bootstrap.version

/**
 * https://developer.android.com/reference/com/google/android/play/core/appupdate/AppUpdateManager.html#startUpdateFlowForResult(com.google.android.play.core.appupdate.AppUpdateInfo,%20android.app.Activity,%20com.google.android.play.core.appupdate.AppUpdateOptions,%20int)
 */
public enum class AppUpdateResultStatus {
  ACCEPTED,
  USER_CANCELLED,
  IN_APP_UPDATE_FAILED,
}
