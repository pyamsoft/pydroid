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

package com.pyamsoft.pydroid.version;

import android.support.annotation.NonNull;
import retrofit2.Call;
import retrofit2.http.Url;
import retrofit2.mock.BehaviorDelegate;

class MockVersionCheckService implements VersionCheckInteractor.VersionCheckService {

  public static final int CURRENT_VERSION = 10;
  @NonNull private final BehaviorDelegate<VersionCheckInteractor.VersionCheckService> delegate;

  MockVersionCheckService(
      @NonNull BehaviorDelegate<VersionCheckInteractor.VersionCheckService> delegate) {
    this.delegate = delegate;
  }

  @NonNull @Override public Call<VersionCheckResponse> checkVersion(@Url String packageName) {
    return delegate.returningResponse(new VersionCheckResponse() {
      @Override int currentVersion() {
        return CURRENT_VERSION;
      }
    }).checkVersion(packageName);
  }
}
