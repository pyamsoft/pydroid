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

package com.pyamsoft.pydroid.core

import androidx.annotation.Keep

/**
 * Adapted from
 * https://source.chromium.org/chromium/chromium/src/+/master:base/android/java/src/org/chromium/base/annotations/RemovableInRelease.java
 *
 * Methods with this annotation will always be removed in release builds. If they cannot be safely
 * removed, then the build will break.
 *
 * "Safely removed" refers to how return values are used. The current ProGuard rules are configured
 * such that:
 * - methods that return an object will always return null.
 * - methods that return boolean will always return false.
 * - methods that return other primitives are removed so long as their return
 * ```
 *    values are not used.
 * ```
 */
@Keep @Target(AnnotationTarget.FUNCTION) public annotation class RemoveInRelease
