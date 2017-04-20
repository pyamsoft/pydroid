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

package com.pyamsoft.pydroid.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.pyamsoft.pydroid.helper.Checker;
import timber.log.Timber;

public final class NetworkUtil {

  private NetworkUtil() {
    throw new RuntimeException("No instances");
  }

  public static void newLink(@NonNull Context c, @NonNull String link) {
    c = Checker.checkNonNull(c);
    link = Checker.checkNonNull(link);

    Uri uri = Uri.parse(link);
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.setData(uri);
    Timber.d("Start intent for URI: %s", uri);
    try {
      c.getApplicationContext().startActivity(intent);
    } catch (Exception e) {
      Timber.e(e, "Error");
      Toast.makeText(c.getApplicationContext(), "No activity available to handle link: " + link,
          Toast.LENGTH_SHORT).show();
    }
  }

  @CheckResult public static boolean hasConnection(@NonNull Context c) {
    c = Checker.checkNonNull(c).getApplicationContext();
    ConnectivityManager connMan =
        (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = connMan.getActiveNetworkInfo();
    return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
  }
}
