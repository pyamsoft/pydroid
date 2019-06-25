/*
 * Copyright 2019 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.core

import androidx.annotation.CheckResult
import kotlinx.coroutines.Job
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@CheckResult
fun singleJob(initializer: () -> Job): SingleJob {
  return singleJob(initializer())
}

@CheckResult
fun singleJob(job: Job = Job()): SingleJob {
  return SingleJob(job)
}

fun Job.tryCancel(): Boolean {
  if (isCancelled) {
    return false
  } else {
    cancel()
    return true
  }
}

class SingleJob internal constructor(
  private var job: Job
) : ReadWriteProperty<Any?, Job> {

  override fun getValue(
    thisRef: Any?,
    property: KProperty<*>
  ): Job {
    return job
  }

  override fun setValue(
    thisRef: Any?,
    property: KProperty<*>,
    value: Job
  ) {
    job.tryCancel()
    job = value
  }

}
