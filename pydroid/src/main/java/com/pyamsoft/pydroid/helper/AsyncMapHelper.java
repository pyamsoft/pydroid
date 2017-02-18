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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.drawable.AsyncMap;
import com.pyamsoft.pydroid.drawable.AsyncMapEntry;

public final class AsyncMapHelper {

  private AsyncMapHelper() {
    throw new RuntimeException("No instances");
  }

  @CheckResult @NonNull public static AsyncMapEntry unsubscribe(@Nullable AsyncMapEntry entry) {
    if (entry == null) {
      return AsyncMap.emptyEntry();
    }

    if (!entry.isUnloaded()) {
      entry.unload();
    }
    return AsyncMap.emptyEntry();
  }
}
