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

package com.pyamsoft.pydroid.theme

import androidx.activity.ComponentActivity
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

private fun noLocalProvidedFor(name: String): Nothing {
  error("CompositionLocal $name not present")
}

/**
 * The Activity held as a Local
 *
 * This is generally only used to provide an entry point to the ObjectGraph via ComposableInjectors
 * In many cases, you don't want to expect or rely on an Activity in Compose.
 */
@JvmField
public val LocalActivity: ProvidableCompositionLocal<ComponentActivity?> = compositionLocalOf {
  noLocalProvidedFor("LocalActivity")
}
