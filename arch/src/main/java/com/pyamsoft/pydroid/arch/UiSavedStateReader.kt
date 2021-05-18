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
import com.pyamsoft.pydroid.arch.internal.BundleUiSavedStateReader

/** Abstraction over restoring data from save-restore lifecycle */
public interface UiSavedStateReader {

  /** Get a saved value at key, null if not present */
  @CheckResult public fun <T : Any> get(key: String): T?
}

/** Convenience function for converting a nullable Bundle into a SavedStateReader */
@CheckResult
public fun Bundle?.toReader(): UiSavedStateReader {
  return BundleUiSavedStateReader(this)
}
