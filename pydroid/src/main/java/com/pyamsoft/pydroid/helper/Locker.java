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
 *
 */

package com.pyamsoft.pydroid.helper;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.concurrent.CountDownLatch;
import timber.log.Timber;

/**
 * {@link Locker} is a simple implementation of a synchronized {@link CountDownLatch}
 *
 * There are two types of Lockers.
 *
 * One is the scoped kind which is created with the
 * static newLock function. This is a locker which is controlled by the scope it
 * is created in.
 *
 * The second type is the Singleton scope. This is a singleton locker which is always present
 * and is not controlled by any kind of scope of creation.
 *
 * An example of intended {@link Locker} usage:
 *
 * Get an instance of your wanted {@link Locker}, Singleton or Scoped
 *
 *
 * onBackgroundThreadPool {
 *
 * // This will wait if any existing lock is still active
 * if (locker.waitForUnlock()) {
 *
 * // This operation will happen only if we waited for lock
 * doFastOperationWithCache();
 *
 * } else {
 *
 * // Prepare the lock to wait for a long operation to finish
 * locker.prepareLock();
 *
 * doLongOperation {
 * // Fill the cache with the results of the long operation
 * fillCache();
 *
 * // Unlock the locker, which will notify any waiting threads.
 * locker.unlock();
 * }
 *
 * }
 */
public class Locker {

  @NonNull private static final Object LOCK = new Object();
  @NonNull private static final Locker GLOBAL = Locker.newLock();
  @Nullable private CountDownLatch latch;

  private Locker() {

  }

  public static void prepareGlobalLock() {
    GLOBAL.prepareLock();
  }

  public static void waitForGlobalUnlock() {
    GLOBAL.waitForUnlock();
  }

  public static void globalUnlock() {
    GLOBAL.unlock();
  }

  /**
   * Creates a new {@link Locker} instance
   *
   * @return New {@link Locker} instance
   */
  @CheckResult @NonNull public static Locker newLock() {
    return new Locker();
  }

  /**
   * Creates a new lock if it does not already exist and prepares for a single operation
   */
  public void prepareLock() {
    if (latch == null) {
      synchronized (LOCK) {
        if (latch == null) {
          Timber.i("Create new lock");
          latch = new CountDownLatch(1);
          return;
        }
      }
    }

    Timber.w("Lock already exists.");
  }

  /**
   * Wait for a lock to finish before clearing the lock
   *
   * @return True if we had a lock, False if we did not
   */
  public boolean waitForUnlock() {
    Object old;
    if (latch != null) {
      synchronized (LOCK) {
        old = latch;
        if (latch != null) {
          Timber.d("Wait for lock...");
          try {
            latch.await();
          } catch (InterruptedException e) {
            Timber.e(e, "Lock Interrupted");
          }
          Timber.i("Unlocked!");
          latch = null;
        }
      }
    } else {
      old = null;
    }

    return (old != null);
  }

  /**
   * Unlocks the underlying lock if it exists
   */
  public void unlock() {
    if (latch == null) {
      throw new IllegalStateException("Must create lock first before you can unlock");
    }

    synchronized (LOCK) {
      Timber.i("Unlock!");
      latch.countDown();
    }
  }
}
