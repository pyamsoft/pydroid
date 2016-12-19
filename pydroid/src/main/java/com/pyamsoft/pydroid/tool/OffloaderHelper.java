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

package com.pyamsoft.pydroid.tool;

import android.support.annotation.Nullable;

public final class OffloaderHelper {

  private OffloaderHelper() {
    throw new RuntimeException("No instances");
  }

  public static void cancel(@Nullable ExecutedOffloader offloader) {
    if (offloader == null) {
      return;
    }

    if (!offloader.isCancelled()) {
      offloader.cancel();
    }
  }

  @SuppressWarnings("unused") public static void cancel(@Nullable ExecutedOffloader... offloaders) {
    if (offloaders == null) {
      return;
    }

    for (final ExecutedOffloader offloader : offloaders) {
      cancel(offloader);
    }
  }
}
