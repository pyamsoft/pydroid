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
abstract class ThreadSafe<T : Any>(protected var obj: T?) {

  /**
   * Underlying lock to block on the instance of the ThreadSafe<T>
   */
  protected val lock = Any()

  abstract class Assignable<T : Any>(obj: T?) : ThreadSafe<T>(obj) {

    /**
     * Guarantee thread safety before writing a new underlying value
     */
    @JvmOverloads fun assign(value: T?, ignoreIfNonNull: Boolean = true) {
      synchronized(lock) {
        if (obj == null || !ignoreIfNonNull) {
          obj = value
        }
      }
    }

  }

  /**
   * ThreadSafe access to objects only in the local function scope
   */
  class Locker<T : Any>(obj: T?) : ThreadSafe<T>(obj) {

    /**
     * Guarantee thread safety and access underlying value in scope
     */
    fun acquire(func: (T) -> Unit) {
      synchronized(lock) {
        obj?.let { func(it) }
      }
    }
  }

  /**
   * ThreadSafe access to objects only in the local function scope
   *
   * Can assign to underlying field from outside of local scope
   */
  class MutableLocker<T : Any>(obj: T?) : Assignable<T>(obj) {

    /**
     * Guarantee thread safety and access underlying value in scope
     */
    fun acquire(func: (T) -> Unit) {
      synchronized(lock) {
        obj?.let { func(it) }
      }
    }
  }

  /**
   * Thread safe access to an underlying singleton parameter
   */
  class Singleton<T : Any>(obj: T?) : ThreadSafe<T>(obj) {

    fun access(): T {
      synchronized(lock) {
        val value = obj
        if (value == null) {
          throw IllegalStateException("Singleton backing object is NULL")
        } else {
          return value
        }
      }
    }
  }

  /**
   * Singleton initialized ahead of time
   */
  class MutableSingleton<T : Any>(obj: T?) : Assignable<T>(obj) {

    fun access(): T {
      synchronized(lock) {
        val value = obj
        if (value == null) {
          throw IllegalStateException("Singleton backing object is NULL")
        } else {
          return value
        }
      }
    }
  }

  /**
   * Singleton that is initialized at getter callsite
   */
  class DynamicSingleton<T : Any>(obj: T?) : ThreadSafe<T>(obj) {

    /**
     * Guarantee thread safety and access underlying value
     */
    fun access(init: () -> T): T {
      synchronized(lock) {
        var value = obj;
        if (value == null) {
          value = init()
          obj = value
        }
        return value
      }
    }
  }
}

