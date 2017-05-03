/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.version;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.helper.Checker;
import io.reactivex.Single;
import timber.log.Timber;

@SuppressWarnings("WeakerAccess") public class VersionCheckInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final VersionCheckService versionCheckService;
  @SuppressWarnings("WeakerAccess") @Nullable Single<VersionCheckResponse> cachedResponse;

  @SuppressWarnings("WeakerAccess")
  public VersionCheckInteractor(@NonNull VersionCheckService versionCheckService) {
    this.versionCheckService = Checker.checkNonNull(versionCheckService);
  }

  /**
   * public
   */
  @NonNull @CheckResult Single<Integer> checkVersion(@NonNull String packageName, boolean force) {
    return Single.defer(() -> {
      Single<VersionCheckResponse> dataSource;
      if (cachedResponse == null || force) {
        Timber.d("Fetch from Network. Force: %s", force);
        dataSource = versionCheckService.checkVersion(Checker.checkNonNull(packageName)).cache();
        cachedResponse = dataSource;
      } else {
        Timber.d("Fetch from cached response");
        dataSource = cachedResponse;
      }

      return dataSource;
    }).map(VersionCheckResponse::currentVersion);
  }
}
