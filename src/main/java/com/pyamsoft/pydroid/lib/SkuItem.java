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
 */

package com.pyamsoft.pydroid.lib;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import org.solovyev.android.checkout.Sku;

@AutoValue abstract class SkuItem {

  @CheckResult @NonNull public static SkuItem create(@NonNull Sku sku, @Nullable String token) {
    return new AutoValue_SkuItem(sku, token);
  }

  @CheckResult public static boolean isConsumable(@NonNull String sku) {
    return sku.contains("donate");
  }

  abstract Sku sku();

  @Nullable abstract String token();

  @CheckResult boolean isPurchased() {
    return token() != null;
  }

  @CheckResult boolean isConsumable() {
    return isConsumable(sku().id);
  }
}
