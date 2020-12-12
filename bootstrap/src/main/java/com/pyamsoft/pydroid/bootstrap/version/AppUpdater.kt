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

package com.pyamsoft.pydroid.bootstrap.version

import androidx.annotation.CheckResult

/**
 * An in-app updater
 */
public interface AppUpdater {

    /**
     * Watch for the update being downloaded
     */
    public suspend fun watchForDownloadComplete(onDownloadComplete: () -> Unit)

    /**
     * Check for a new update
     */
    @CheckResult
    public suspend fun checkForUpdate(): AppUpdateLauncher

    /**
     * Complete the update, which will restart the application
     */
    public suspend fun complete()
}
