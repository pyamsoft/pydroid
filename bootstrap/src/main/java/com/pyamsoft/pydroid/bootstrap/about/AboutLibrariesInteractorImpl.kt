/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.bootstrap.about

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibraries
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.core.threads.Enforcer
import io.reactivex.Observable
import io.reactivex.Single

internal class AboutLibrariesInteractorImpl internal constructor(
  private val enforcer: Enforcer
) : AboutLibrariesInteractor {

  @CheckResult
  private fun createLicenseStream(): Single<Set<OssLibrary>> {
    return Single.fromCallable {
      enforcer.assertNotOnMainThread()
      return@fromCallable OssLibraries.libraries()
    }
  }

  override fun loadLicenses(bypass: Boolean): Single<List<OssLibrary>> {
    return createLicenseStream()
        .flatMapObservable {
          enforcer.assertNotOnMainThread()
          return@flatMapObservable Observable.fromIterable(it)
        }
        .toSortedList { o1, o2 ->
          enforcer.assertNotOnMainThread()
          return@toSortedList o1.name.compareTo(o2.name, ignoreCase = true)
        }
  }
}
