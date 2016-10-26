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
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.about.LicenseProvider;
import timber.log.Timber;

public abstract class SingleInitContentProvider extends ContentProvider implements LicenseProvider {

  @Nullable private static SingleInitContentProvider instance;
  private static boolean created;

  static {
    created = false;
    instance = null;
  }

  @Nullable private ModuleDelegate delegate;

  private static void setCreated(boolean created) {
    SingleInitContentProvider.created = created;
  }

  @NonNull @CheckResult static LicenseProvider getLicenseProvider() {
    if (instance == null) {
      throw new NullPointerException("Instance is NULL. Was this CP never created?");
    }
    return instance;
  }

  @NonNull @CheckResult static ModuleDelegate getInstance() {
    if (instance == null) {
      throw new NullPointerException("Instance is NULL. Was this CP never created?");
    }
    return instance.getDelegate();
  }

  private static void setInstance(@NonNull SingleInitContentProvider instance) {
    SingleInitContentProvider.instance = instance;
  }

  @NonNull @CheckResult ModuleDelegate getDelegate() {
    if (delegate == null) {
      throw new NullPointerException("ModuleDelegate is NULL. Was this CP never created?");
    }
    return delegate;
  }

  @Override public final boolean onCreate() {
    if (created) {
      Timber.e("Already created, do nothing");
      return false;
    }

    Timber.i("Create pyamsoft application");
    setCreated(true);
    final Context context = getContext();
    if (context == null) {
      throw new NullPointerException("Context is NULL");
    }

    onFirstCreateProtected(context);
    onFirstCreate(context);
    setInstance(this);
    return false;
  }

  private void onFirstCreateProtected(@NonNull Context context) {
    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
      setStrictMode();
      onFirstCreateInDebugMode(context);
    }

    delegate = new ModuleDelegate(new PYDroidModule(context, this));
    onFirstCreate(context);
  }

  @SuppressWarnings({ "WeakerAccess", "EmptyMethod" })
  protected void onFirstCreate(@NonNull Context context) {
  }

  @SuppressWarnings({ "WeakerAccess", "EmptyMethod" })
  protected void onFirstCreateInDebugMode(@NonNull Context context) {
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

  static class ModuleDelegate {

    @NonNull private final PYDroidModule module;

    ModuleDelegate(@NonNull PYDroidModule module) {
      //noinspection ConstantConditions
      if (module == null) {
        throw new NullPointerException("Module is NULL");
      }
      this.module = module;
    }

    @CheckResult @NonNull PYDroidModule getModule() {
      return module;
    }
  }
}
