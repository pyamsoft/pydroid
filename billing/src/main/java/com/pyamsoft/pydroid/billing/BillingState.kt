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

package com.pyamsoft.pydroid.billing

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

/** State of the billing client */
@Stable
@Immutable
public enum class BillingState {
  /** Billing client is still loading, state unknown */
  LOADING,

  /** Billing client is connected and active */
  CONNECTED,

  /** Billing client is currently disconnected but may become active again later. */
  DISCONNECTED
}
