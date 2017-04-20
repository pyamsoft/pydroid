/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.about;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import com.google.auto.value.AutoValue;

@RestrictTo(RestrictTo.Scope.LIBRARY) @AutoValue public abstract class AboutLibrariesModel {

  @CheckResult @NonNull
  public static AboutLibrariesModel create(@NonNull String name, @NonNull String homepage,
      @NonNull String license) {
    return new AutoValue_AboutLibrariesModel(name, homepage, license, "");
  }

  @CheckResult @NonNull
  public static AboutLibrariesModel createWithContent(@NonNull String name, @NonNull String homepage,
      @NonNull String content) {
    return new AutoValue_AboutLibrariesModel(name, homepage, "", content);
  }

  @CheckResult public abstract String name();

  @CheckResult public abstract String homepage();

  @CheckResult public abstract String license();

  @CheckResult public abstract String customContent();
}
