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

public abstract class BuildConfigChecker {

  @Nullable private static volatile BuildConfigChecker instance = null;

  protected BuildConfigChecker() {
  }

  @CheckResult @NonNull public static BuildConfigChecker getInstance() {
    if (instance == null) {
      throw new IllegalStateException("BuildConfigChecker instance is NULL");
    } else {
      //noinspection ConstantConditions
      return instance;
    }
  }

  public static void setInstance(@NonNull BuildConfigChecker checker) {
    instance = Checker.checkNonNull(checker);
  }

  @CheckResult public abstract boolean isDebugMode();
}
