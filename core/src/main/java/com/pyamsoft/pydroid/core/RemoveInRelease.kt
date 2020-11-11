package com.pyamsoft.pydroid.core

import androidx.annotation.Keep

/**
 * Adapted from https://source.chromium.org/chromium/chromium/src/+/master:base/android/java/src/org/chromium/base/annotations/RemovableInRelease.java
 *
 * Methods with this annotation will always be removed in release builds.
 * If they cannot be safely removed, then the build will break.
 *
 * "Safely removed" refers to how return values are used. The current
 * ProGuard rules are configured such that:
 *  - methods that return an object will always return null.
 *  - methods that return boolean will always return false.
 *  - methods that return other primitives are removed so long as their return
 *    values are not used.
 */

@Keep
@Target(AnnotationTarget.FUNCTION)
annotation class RemoveInRelease