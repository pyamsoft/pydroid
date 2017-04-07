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

package com.pyamsoft.pydroid.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import com.pyamsoft.pydroid.helper.Checker;
import timber.log.Timber;

public final class AppUtil {

  private AppUtil() {
    throw new RuntimeException("No instances");
  }

  @CheckResult @NonNull public static Intent getApplicationInfoIntent(@NonNull String packageName) {
    packageName = Checker.checkNonNull(packageName);
    final Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    i.addCategory(Intent.CATEGORY_DEFAULT);
    i.setData(Uri.fromParts("package", packageName, null));
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    return i;
  }

  @CheckResult public static float convertToDP(@NonNull Context c, float px) {
    c = Checker.checkNonNull(c);
    final DisplayMetrics m = c.getApplicationContext().getResources().getDisplayMetrics();
    final float dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, m);
    Timber.d("Convert %f px to %f dp", px, dp);
    return dp;
  }
}
