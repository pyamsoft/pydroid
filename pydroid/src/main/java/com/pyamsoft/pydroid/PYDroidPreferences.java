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

package com.pyamsoft.pydroid;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import com.pyamsoft.pydroid.helper.Checker;

@RestrictTo(RestrictTo.Scope.LIBRARY) public interface PYDroidPreferences {

  @CheckResult int getRatingAcceptedVersion();

  void setRatingAcceptedVersion(int version);

  class Instance {

    @Nullable private static volatile PYDroidPreferences instance = null;

    /**
     * Retrieve the singleton instance of PYDroidPreferences
     *
     * Guarantee that the singleton is created and non null using double checking synchronization
     */
    @CheckResult @NonNull public static PYDroidPreferences getInstance(@NonNull Context context) {
      context = Checker.checkNonNull(context);
      if (instance == null) {
        synchronized (Instance.class) {
          if (instance == null) {
            instance = new PYDroidPreferencesImpl(context.getApplicationContext());
          }
        }
      }

      return Checker.checkNonNull(instance);
    }
  }
}
