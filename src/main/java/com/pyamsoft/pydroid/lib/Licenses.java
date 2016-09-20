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
import com.google.auto.value.AutoValue;

import static com.pyamsoft.pydroid.lib.Licenses.Id.ANDROID;
import static com.pyamsoft.pydroid.lib.Licenses.Id.ANDROID_CHECKOUT;
import static com.pyamsoft.pydroid.lib.Licenses.Id.ANDROID_PRIORITY_JOBQUEUE;
import static com.pyamsoft.pydroid.lib.Licenses.Id.ANDROID_SUPPORT;
import static com.pyamsoft.pydroid.lib.Licenses.Id.AUTO_VALUE;
import static com.pyamsoft.pydroid.lib.Licenses.Id.BUTTERKNIFE;
import static com.pyamsoft.pydroid.lib.Licenses.Id.DAGGER;
import static com.pyamsoft.pydroid.lib.Licenses.Id.FAST_ADAPTER;
import static com.pyamsoft.pydroid.lib.Licenses.Id.FIREBASE;
import static com.pyamsoft.pydroid.lib.Licenses.Id.GOOGLE_PLAY_SERVICES;
import static com.pyamsoft.pydroid.lib.Licenses.Id.LEAK_CANARY;
import static com.pyamsoft.pydroid.lib.Licenses.Id.PYDROID;
import static com.pyamsoft.pydroid.lib.Licenses.Id.RETROFIT2;
import static com.pyamsoft.pydroid.lib.Licenses.Id.RXANDROID;
import static com.pyamsoft.pydroid.lib.Licenses.Id.RXJAVA;
import static com.pyamsoft.pydroid.lib.Licenses.Id.SQLBRITE;
import static com.pyamsoft.pydroid.lib.Licenses.Id.SQLDELIGHT;

@AutoValue public abstract class Licenses {

  public enum Id {
    EMPTY,
    GOOGLE_PLAY_SERVICES,
    ANDROID,
    ANDROID_SUPPORT,
    PYDROID,
    RXJAVA,
    RXANDROID,
    LEAK_CANARY,
    FIREBASE,
    ANDROID_CHECKOUT,
    FAST_ADAPTER,
    BUTTERKNIFE,
    AUTO_VALUE,
    DAGGER,
    RETROFIT2,
    SQLBRITE,
    SQLDELIGHT,
    ANDROID_PRIORITY_JOBQUEUE,
  }

  @NonNull static Licenses googlePlayItem() {
    return create(GOOGLE_PLAY_SERVICES, "");
  }

  @NonNull static Licenses androidItem() {
    return create(ANDROID, "licenses/android");
  }

  @NonNull static Licenses androidSupportItem() {
    return create(ANDROID_SUPPORT, "licenses/androidsupport");
  }

  @NonNull static Licenses pydroidItem() {
    return create(PYDROID, "licenses/pydroid");
  }

  @NonNull static Licenses rxjavaItem() {
    return create(RXJAVA, "licenses/rxjava");
  }

  @NonNull static Licenses rxandroidItem() {
    return create(RXANDROID, "licenses/rxandroid");
  }

  @NonNull static Licenses leakcanaryItem() {
    return create(LEAK_CANARY, "licenses/leakcanary");
  }

  @NonNull static Licenses firebaseItem() {
    return create(FIREBASE, "licenses/firebase");
  }

  @NonNull static Licenses androidCheckoutItem() {
    return create(ANDROID_CHECKOUT, "licenses/androidcheckout");
  }
  @NonNull static Licenses butterknifeItem() {
    return create(BUTTERKNIFE, "licenses/butterknife");
  }

  @NonNull static Licenses autovalueItem() {
    return create(AUTO_VALUE, "licenses/autovalue");
  }

  @NonNull static Licenses daggerItem() {
    return create(DAGGER, "licenses/dagger2");
  }

  @NonNull static Licenses retrofitItem() {
    return create(RETROFIT2, "licenses/retrofit");
  }

  @NonNull static Licenses sqlbriteItem() {
    return create(SQLBRITE, "licenses/sqlbrite");
  }

  @NonNull static Licenses sqldelightItem() {
    return create(SQLDELIGHT, "licenses/sqldelight");
  }

  @NonNull static Licenses androidPriorityJobQueueItem() {
    return create(ANDROID_PRIORITY_JOBQUEUE, "licenses/androidpriorityjobqueue");
  }

  @NonNull static Licenses fastAdapterItem() {
    return create(FAST_ADAPTER, "licenses/fastadapter");
  }

  @NonNull @CheckResult public static Licenses create(@NonNull Id id, @NonNull String location) {
    return new AutoValue_Licenses(id, location);
  }

  public abstract Id id();

  public abstract String location();
}




