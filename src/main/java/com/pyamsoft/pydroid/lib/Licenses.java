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

@AutoValue public abstract class Licenses {

  public static final int EMPTY = 0;
  public static final int GOOGLE_PLAY_SERVICES = 1;
  public static final int ANDROID = 2;
  public static final int ANDROID_SUPPORT = 3;
  public static final int PYDROID = 4;
  public static final int RXJAVA = 5;
  public static final int RXANDROID = 6;
  public static final int LEAK_CANARY = 7;
  public static final int FIREBASE = 8;
  public static final int ANDROID_CHECKOUT = 9;
  public static final int FAST_ADAPTER = 10;
  public static final int BUTTERKNIFE = 11;
  public static final int AUTO_VALUE = 12;
  public static final int DAGGER = 13;
  public static final int RETROFIT2 = 14;
  public static final int SQLBRITE = 15;
  public static final int SQLDELIGHT = 16;
  public static final int ANDROID_PRIORITY_JOBQUEUE = 17;

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

  @NonNull @CheckResult public static Licenses create(int id, @NonNull String location) {
    return new AutoValue_Licenses(id, location);
  }

  public abstract int id();

  public abstract String location();
}




