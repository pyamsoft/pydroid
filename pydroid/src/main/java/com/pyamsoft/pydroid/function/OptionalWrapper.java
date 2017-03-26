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

package com.pyamsoft.pydroid.function;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import com.pyamsoft.pydroid.helper.Checker;

@AutoValue public abstract class OptionalWrapper<T> {

  @CheckResult @NonNull public static <T> OptionalWrapper<T> ofNullable(@Nullable T source) {
    return new AutoValue_OptionalWrapper<>(source);
  }

  @CheckResult @Nullable abstract T source();

  @CheckResult public final boolean isPresent() {
    return source() != null;
  }

  @CheckResult @NonNull public final T item() {
    return Checker.checkNonNull(source());
  }
}
