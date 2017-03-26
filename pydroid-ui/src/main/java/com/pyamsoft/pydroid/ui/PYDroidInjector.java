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

package com.pyamsoft.pydroid.ui;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import com.pyamsoft.pydroid.helper.Checker;

@RestrictTo(RestrictTo.Scope.LIBRARY) public class PYDroidInjector
    implements IPYDroidApp<PYDroidComponent> {

  @Nullable private static volatile PYDroidInjector instance = null;
  @NonNull private final PYDroidComponent component;

  private PYDroidInjector(@NonNull PYDroidComponent component) {
    this.component = Checker.checkNonNull(component);
  }

  static void set(@Nullable PYDroidComponent component) {
    instance = new PYDroidInjector(Checker.checkNonNull(component));
  }

  @NonNull @CheckResult public static PYDroidInjector get() {
    return Checker.checkNonNull(instance);
  }

  @NonNull @Override public PYDroidComponent provideComponent() {
    return component;
  }
}
