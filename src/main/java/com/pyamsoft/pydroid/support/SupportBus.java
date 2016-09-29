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

package com.pyamsoft.pydroid.support;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.pyamsoft.pydroid.tool.Bus;
import com.pyamsoft.pydroid.tool.BusImpl;

final class SupportBus implements Bus<DonationResult> {

  @NonNull private static final SupportBus bus = new SupportBus();
  @NonNull private Bus<DonationResult> delegate;

  private SupportBus() {
    delegate = new BusImpl<>();
  }

  @VisibleForTesting static void set(@NonNull BusImpl<DonationResult> delegate) {
    bus.setDelegate(delegate);
  }

  @CheckResult @NonNull public static SupportBus get() {
    return bus;
  }

  @VisibleForTesting void setDelegate(@NonNull Bus<DonationResult> delegate) {
    this.delegate = delegate;
  }

  @Override public void post(@NonNull DonationResult event) {
    delegate.post(event);
  }

  @NonNull @Override public Event<DonationResult> register(@NonNull Event<DonationResult> onCall) {
    return delegate.register(onCall);
  }

  @NonNull @Override public Event<DonationResult> register(@NonNull Event<DonationResult> onCall,
      @Nullable Error onError) {
    return delegate.register(onCall, onError);
  }

  @Override public void unregister(@Nullable Event<DonationResult> onCall) {
    delegate.unregister(onCall);
  }
}
