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

package com.pyamsoft.pydroid.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import timber.log.Timber;

public final class NetworkUtil {

  private NetworkUtil() {

  }

  public static void newLink(final @NonNull Context c, final @NonNull String link) {
    final Uri uri = Uri.parse(link);
    final Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.setData(uri);
    Timber.d("Start intent for URI: %s", uri);
    c.getApplicationContext().startActivity(intent);
  }

  public static boolean hasConnection(final @NonNull Context c) {
    final Context context = c.getApplicationContext();
    final ConnectivityManager connMan =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = connMan.getActiveNetworkInfo();
    final boolean connected = (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    Timber.d("Check network availability: %s", connected);
    return connected;
  }
}
