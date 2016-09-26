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

package com.pyamsoft.pydroid;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.StrictMode;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import timber.log.Timber;

public abstract class SingleInitContentProvider extends ContentProvider {

  private static boolean created;

  static {
    created = false;
  }

  @Override public boolean onCreate() {
    if (created) {
      return false;
    }

    created = true;
    final Context context = getContext();
    if (context == null) {
      throw new NullPointerException("Context is NULL");
    }

    final Context appContext = context.getApplicationContext();
    onFirstCreate(appContext);
    return false;
  }

  /**
   * In a Firebase multi process app, this block of code will be guaranteed to only run on the
   * first
   * application creation instance
   */
  @CallSuper protected void onFirstCreate(@NonNull Context context) {
    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
      setStrictMode();
      onFirstCreateInDebugMode(context);
    }
  }

  private void setStrictMode() {
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll()
        .penaltyLog()
        .penaltyDeath()
        .permitDiskReads()
        .permitDiskWrites()
        .penaltyFlashScreen()
        .build());
    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
  }

  @SuppressWarnings({ "WeakerAccess", "EmptyMethod" })
  protected void onFirstCreateInDebugMode(@NonNull Context context) {

  }

  @Nullable @Override
  public final Cursor query(@NonNull Uri uri, String[] projection, String selection,
      String[] selectionArgs, String sortOrder) {
    throw new RuntimeException("This is not actually a content provider");
  }

  @Nullable @Override public final String getType(@NonNull Uri uri) {
    throw new RuntimeException("This is not actually a content provider");
  }

  @Nullable @Override public final Uri insert(@NonNull Uri uri, ContentValues values) {
    throw new RuntimeException("This is not actually a content provider");
  }

  @Override public final int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
    throw new RuntimeException("This is not actually a content provider");
  }

  @Override public final int update(@NonNull Uri uri, ContentValues values, String selection,
      String[] selectionArgs) {
    throw new RuntimeException("This is not actually a content provider");
  }
}
