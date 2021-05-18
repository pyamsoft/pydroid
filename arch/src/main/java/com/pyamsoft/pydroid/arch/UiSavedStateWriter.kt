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

package com.pyamsoft.pydroid.arch

import android.os.Bundle
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.arch.internal.BundleUiSavedStateWriter

/** Abstraction over saving data into the save-restore lifecycle */
public interface UiSavedStateWriter {

  /** Save a value at key */
  public fun <T : Any> put(key: String, value: T)

  /** Remove a value at key */
  public fun <T : Any> remove(key: String): T?
}

/** Convenience function for converting a Bundle into a SavedStateWriter */
@CheckResult
public fun Bundle.toWriter(): UiSavedStateWriter {
  return BundleUiSavedStateWriter(this)
}
