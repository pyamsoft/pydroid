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
 */

package com.pyamsoft.pydroid.helper

/**
 * Small collection of thread safe helpers for interacting with objects
 */
abstract class ThreadSafe<T>(protected var obj: T) {

  /**
   * Underlying lock to block on the instance of the ThreadSafe<T>
   */
  protected val lock = Any()

  /**
   * Guarantee thread safety before writing a new underlying value
   */
  @JvmOverloads fun assign(value: T, ignoreIfNonNull: Boolean = true) {
    synchronized(lock) {
      if (obj == null || !ignoreIfNonNull) {
        obj = value
      }
    }
  }

  /**
   * ThreadSafe access to objects only in the local function scope
   */
  class Locker<T>(obj: T) : ThreadSafe<T>(obj) {

    /**
     * Guarantee thread safety and access underlying value in scope
     */
    fun acquire(func: (T) -> Unit) {
      synchronized(lock) {
        func(obj)
      }
    }
  }

  /**
   * Thread safe access to an underlying singleton parameter
   */
  class Singleton<T>(obj: T) : ThreadSafe<T>(obj) {

    /**
     * Guarantee thread safety and access underlying value
     */
    fun access(): T {
      synchronized(lock) {
        return obj
      }
    }
  }

}

