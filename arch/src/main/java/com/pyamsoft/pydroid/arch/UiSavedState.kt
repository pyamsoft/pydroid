/*
 * Copyright 2019 Peter Kenji Yamanaka
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
 *
 */

package com.pyamsoft.pydroid.arch

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle

class UiSavedState internal constructor(bundle: Bundle?) {

    private val bundle: Bundle?

    init {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            this.bundle = bundle?.deepCopy()
        } else {
            this.bundle = bundle?.let { Bundle(it) }
        }
    }

    fun <T : Any> consume(
        key: String,
        defaultValue: T,
        consumer: (value: T) -> Unit
    ) {
        consume(key, validConsumer = consumer, invalidConsumer = { consumer(defaultValue) })
    }

    fun <T : Any> consume(
        key: String,
        consumer: (value: T) -> Unit
    ) {
        consume(key, validConsumer = consumer, invalidConsumer = {})
    }

    private inline fun <T : Any> consume(
        key: String,
        validConsumer: (value: T) -> Unit,
        invalidConsumer: () -> Unit
    ) {
        val b = bundle
        if (b == null) {
            invalidConsumer()
        } else {
            if (!b.containsKey(key)) {
                invalidConsumer()
            } else {
                @Suppress("UNCHECKED_CAST")
                val value = b.get(key) as? T
                b.remove(key)
                if (value == null) {
                    invalidConsumer()
                } else {
                    validConsumer(value)
                }
            }
        }
    }

    companion object {

        internal val EMPTY = UiSavedState(null)
    }
}
