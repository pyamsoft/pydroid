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

package com.pyamsoft.pydroid.about;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.model.Licenses;

final class AboutItemsUtil {

  private AboutItemsUtil() {
    throw new RuntimeException("No instances");
  }

  @NonNull @CheckResult public static AboutItem licenseForAndroid() {
    return new AboutItem("Android", "https://source.android.com", Licenses.ANDROID);
  }

  @NonNull @CheckResult public static AboutItem licenseForPYDroid() {
    return new AboutItem("PYDroid", "https://pyamsoft.github.io/pydroid", Licenses.PYDROID);
  }
}
