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

package com.pyamsoft.pydroid.donate;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import com.google.auto.value.AutoValue;
import org.solovyev.android.checkout.Sku;

@RestrictTo(RestrictTo.Scope.LIBRARY) @AutoValue public abstract class SkuModel {

  @CheckResult @NonNull public static SkuModel create(@NonNull Sku sku, @Nullable String token) {
    return new AutoValue_SkuModel(sku, token);
  }

  public abstract Sku sku();

  @Nullable public abstract String token();
}
