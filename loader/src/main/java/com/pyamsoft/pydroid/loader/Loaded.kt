/*
 * Copyright 2020 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.loader

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.util.doOnDestroy

/**
 * A representation of an image target which is currently loaded
 */
public interface Loaded {

    /**
     * Dispose of any data loaded into an image target
     */
    public fun dispose()
}

/**
 * Dispose the Loaded resource once the lifecycle hits destroy
 */
public fun Loaded.disposeOnDestroy(owner: LifecycleOwner) {
    this.disposeOnDestroy(owner.lifecycle)
}

/**
 * Dispose the Loaded resource once the lifecycle hits destroy
 */
public fun Loaded.disposeOnDestroy(lifecycle: Lifecycle) {
    lifecycle.doOnDestroy { this.dispose() }
}
