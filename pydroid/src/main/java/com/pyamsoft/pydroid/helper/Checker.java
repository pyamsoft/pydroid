/*
 * Copyright 2016 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.helper;

import android.support.annotation.NonNull;

public final class Checker {

  private Checker() {
    throw new RuntimeException("Np instances");
  }

  @NonNull public static <T> T checkNonNull(T source) {
    if (source == null) {
      throw new IllegalStateException("Object cannot be NULL");
    }

    return source;
  }
}
