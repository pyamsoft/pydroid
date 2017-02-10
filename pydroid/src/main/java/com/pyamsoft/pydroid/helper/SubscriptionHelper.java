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

package com.pyamsoft.pydroid.helper;

import android.support.annotation.Nullable;
import rx.Subscription;

public final class SubscriptionHelper {

  private SubscriptionHelper() {
    throw new RuntimeException("No instances");
  }

  public static void unsubscribe(@Nullable Subscription subscription) {
    if (subscription == null) {
      return;
    }

    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  public static void unsubscribe(@Nullable Subscription... subscriptions) {
    if (subscriptions == null) {
      return;
    }

    for (final Subscription subscription : subscriptions) {
      unsubscribe(subscription);
    }
  }
}
