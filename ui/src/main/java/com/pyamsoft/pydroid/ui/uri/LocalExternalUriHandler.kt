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

package com.pyamsoft.pydroid.ui.uri

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.pyamsoft.pydroid.ui.internal.uri.ExternalUriHandlerBridge

/**
 * The ExternalUriHandler
 *
 * This should be initialized to something not-null by the ExternalUriHandlerBridge via
 * PYDroidActivityDelegateInternal before any View layer items actually call for it, so it should
 * not throw.
 */
@JvmField
public val LocalExternalUriHandler: ProvidableCompositionLocal<ExternalUriHandler> =
    staticCompositionLocalOf {
      ExternalUriHandlerBridge.provideHandler()
    }
