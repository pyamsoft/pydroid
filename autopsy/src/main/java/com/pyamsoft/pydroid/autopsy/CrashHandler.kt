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

package com.pyamsoft.pydroid.autopsy

import android.content.Context
import kotlin.system.exitProcess

internal data class CrashHandler internal constructor(
    private val context: Context
) : Thread.UncaughtExceptionHandler {

    private val logger = Logger.tag(this)

    override fun uncaughtException(t: Thread, e: Throwable) {
        logger.e(e, "Uncaught Exception!")
        try {
            logger.d("Launching uncaught exception CrashActivity")
            context.startActivity(CrashActivity.newIntent(context, t.name, e))
        } catch (throwable: Throwable) {
            logger.e(throwable, "Error during exception processing")
        } finally {
            logger.d("Completed exception processing")

            // NOTE: Sometimes this exit will occur before the Activity launches correctly.
            exitProcess(1)
        }
    }
}
