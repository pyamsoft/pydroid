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
import androidx.startup.Initializer

/**
 * Automatically initialized itself on startup via ContentProvider in AndroidManifest
 *
 * Overrides the default thread exception handler to instead launch the CrashActivity
 */
internal class AutopsyInitializer internal constructor() : Initializer<Boolean> {

    private val logger = Logger.tag(this)

    override fun create(context: Context): Boolean {
        logger.d("Creating initializer and overriding crash handler")
        val handler = CrashHandler(context.applicationContext)
        Thread.setDefaultUncaughtExceptionHandler(handler)
        return true
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}
