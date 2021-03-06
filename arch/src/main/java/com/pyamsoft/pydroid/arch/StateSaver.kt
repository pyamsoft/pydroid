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

/** An interface which can save the state of an object into a Bundle */
public interface StateSaver {

  /** Save the state of the object into the given Bundle */
  public fun saveState(outState: Bundle)

  /** Save the state of the object into the given UiSavedStateWriter */
  public fun saveState(outState: UiSavedStateWriter)
}
