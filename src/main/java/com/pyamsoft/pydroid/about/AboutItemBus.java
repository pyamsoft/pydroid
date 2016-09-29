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

package com.pyamsoft.pydroid.about;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.android.annotations.VisibleForTesting;
import com.pyamsoft.pydroid.tool.Bus;
import com.pyamsoft.pydroid.tool.BusImpl;

final class AboutItemBus implements Bus<AboutLicenseLoadEvent> {

  @NonNull private static final AboutItemBus bus = new AboutItemBus();
  @NonNull private Bus<AboutLicenseLoadEvent> delegate;

  private AboutItemBus() {
    delegate = new BusImpl<>();
  }

  @CheckResult @NonNull public static AboutItemBus get() {
    return bus;
  }

  @VisibleForTesting static void setBus(@NonNull Bus<AboutLicenseLoadEvent> delegate) {
    bus.setDelegate(delegate);
  }

  @VisibleForTesting void setDelegate(@NonNull Bus<AboutLicenseLoadEvent> delegate) {
    this.delegate = delegate;
  }

  @Override public void post(@NonNull AboutLicenseLoadEvent event) {
    delegate.post(event);
  }

  @NonNull @Override
  public Event<AboutLicenseLoadEvent> register(@NonNull Event<AboutLicenseLoadEvent> onCall) {
    return delegate.register(onCall);
  }

  @NonNull @Override
  public Event<AboutLicenseLoadEvent> register(@NonNull Event<AboutLicenseLoadEvent> onCall,
      @Nullable Error onError) {
    return delegate.register(onCall, onError);
  }

  @Override public void unregister(@Nullable Event<AboutLicenseLoadEvent> onCall) {
    delegate.unregister(onCall);
  }
}
