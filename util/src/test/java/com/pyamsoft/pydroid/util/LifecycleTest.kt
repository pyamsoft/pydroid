/*
 * Copyright 2026 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.util

import android.os.Build
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(
    // Need this here since Robolectric does not yet support API 37 (which is default otherwise)
    minSdk = Build.VERSION_CODES.O,
    maxSdk = Build.VERSION_CODES.BAKLAVA,
)
public class LifecycleTest {

  private class FakeOwner : LifecycleOwner {
    override val lifecycle: Lifecycle = LifecycleRegistry(this)
    val registry: LifecycleRegistry
      get() = lifecycle as LifecycleRegistry
  }

  @Test
  public fun doOnCreate_firesOnceOnCreate() {
    val owner = FakeOwner()
    var count = 0
    owner.doOnCreate { count++ }

    owner.registry.currentState = Lifecycle.State.CREATED
    owner.registry.currentState = Lifecycle.State.STARTED
    owner.registry.currentState = Lifecycle.State.RESUMED

    assertEquals(1, count)
  }

  @Test
  public fun doOnDestroy_firesOnceOnDestroy() {
    val owner = FakeOwner()
    var count = 0
    owner.doOnDestroy { count++ }

    owner.registry.currentState = Lifecycle.State.CREATED
    owner.registry.currentState = Lifecycle.State.DESTROYED

    assertEquals(1, count)
  }

  @Test
  public fun doOnStart_firesOnlyOnceAcrossRepeatedStarts() {
    val owner = FakeOwner()
    var count = 0
    owner.doOnStart { count++ }

    owner.registry.currentState = Lifecycle.State.CREATED
    owner.registry.currentState = Lifecycle.State.STARTED
    owner.registry.currentState = Lifecycle.State.CREATED
    owner.registry.currentState = Lifecycle.State.STARTED

    assertEquals(1, count)
  }

  @Test
  public fun doOnStop_notTriggeredByOtherEvents() {
    val owner = FakeOwner()
    var count = 0
    owner.doOnStop { count++ }

    owner.registry.currentState = Lifecycle.State.CREATED
    owner.registry.currentState = Lifecycle.State.STARTED
    owner.registry.currentState = Lifecycle.State.RESUMED

    assertEquals(0, count)
  }

  @Test
  public fun doOnResume_firesOnlyOnceAcrossRepeatedResumes() {
    val owner = FakeOwner()
    var count = 0
    owner.doOnResume { count++ }

    owner.registry.currentState = Lifecycle.State.CREATED
    owner.registry.currentState = Lifecycle.State.STARTED
    owner.registry.currentState = Lifecycle.State.RESUMED
    owner.registry.currentState = Lifecycle.State.STARTED
    owner.registry.currentState = Lifecycle.State.RESUMED

    assertEquals(1, count)
  }

  @Test
  public fun doOnPause_firesOnlyOnceAcrossRepeatedPauses() {
    val owner = FakeOwner()
    var count = 0
    owner.doOnPause { count++ }

    owner.registry.currentState = Lifecycle.State.CREATED
    owner.registry.currentState = Lifecycle.State.STARTED
    owner.registry.currentState = Lifecycle.State.RESUMED
    owner.registry.currentState = Lifecycle.State.STARTED
    owner.registry.currentState = Lifecycle.State.RESUMED
    owner.registry.currentState = Lifecycle.State.STARTED

    assertEquals(1, count)
  }
}
