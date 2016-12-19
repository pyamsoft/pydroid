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

package com.pyamsoft.pydroidrx;

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.tool.AsyncMap;
import rx.Subscription;

class AsyncDrawableSubscriptionEntry implements AsyncMap.Entry {

  @NonNull private final Subscription subscription;

  AsyncDrawableSubscriptionEntry(@NonNull Subscription subscription) {
    //noinspection ConstantConditions
    if (subscription == null) {
      throw new NullPointerException("Subscription cannot be NULL");
    }
    this.subscription = subscription;
  }

  @Override public void unload() {
    if (!isUnloaded()) {
      subscription.unsubscribe();
    }
  }

  @Override public boolean isUnloaded() {
    return subscription.isUnsubscribed();
  }
}
