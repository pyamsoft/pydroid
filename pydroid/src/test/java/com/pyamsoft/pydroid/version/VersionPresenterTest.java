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

package com.pyamsoft.pydroid.version;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import retrofit2.Retrofit;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

import static org.junit.Assert.assertEquals;

public class VersionPresenterTest {

  private VersionCheckPresenterImpl presenter;
  @Mock private MockRetrofit mockRetrofit;

  /**
   * Create a mock retrofit service and use it to make a presenter
   */
  @Before public void setup() {
    final NetworkBehavior behavior = NetworkBehavior.create();
    final Retrofit retrofit = new Retrofit.Builder().baseUrl("http://example.com").build();
    mockRetrofit = new MockRetrofit.Builder(retrofit).networkBehavior(behavior).build();

    final BehaviorDelegate<VersionCheckInteractor.VersionCheckService> delegate =
        mockRetrofit.create(VersionCheckInteractor.VersionCheckService.class);
    final MockVersionCheckService service = new MockVersionCheckService(delegate);

    final VersionCheckInteractorImpl fakeInteractor =
        new VersionCheckInteractorImpl(service, "com.example.example");
    presenter = new VersionCheckPresenterImpl(fakeInteractor);
  }

  /**
   * Check for updates, and expect no update
   *
   * @throws InterruptedException
   */
  @Test public void testCheckForUpdates_UpToDate() throws InterruptedException {
    // No failure
    mockRetrofit.networkBehavior().setFailurePercent(0);

    final CountDownLatch latch = new CountDownLatch(1);
    presenter.bindView(new VersionCheckPresenter.View() {
      @Override public void onVersionCheckFinished() {
        latch.countDown();
      }

      @Override public void onUpdatedVersionFound(int oldVersionCode, int updatedVersionCode) {
        throw new AssertionError("Version should not report out of date");
      }
    });

    presenter.checkForUpdates(MockVersionCheckService.CURRENT_VERSION);
    if (!latch.await(10, TimeUnit.SECONDS)) {
      throw new RuntimeException("Latch did not count down within ten seconds");
    }
  }

  /**
   * Test for updates and expect an available update
   *
   * @throws InterruptedException
   */
  @Test public void testCheckForUpdates_OutOfDate() throws InterruptedException {
    // No failure
    mockRetrofit.networkBehavior().setFailurePercent(0);

    final CountDownLatch latch = new CountDownLatch(1);
    presenter.bindView(new VersionCheckPresenter.View() {
      @Override public void onVersionCheckFinished() {
      }

      @Override public void onUpdatedVersionFound(int oldVersionCode, int updatedVersionCode) {
        latch.countDown();
      }
    });

    presenter.checkForUpdates(MockVersionCheckService.CURRENT_VERSION - 2);
    if (!latch.await(10, TimeUnit.SECONDS)) {
      throw new RuntimeException("Latch did not count down within ten seconds");
    }
  }

  @Test public void testCheckForUpdates_NetworkFailure() throws InterruptedException {
    // Fail
    mockRetrofit.networkBehavior().setFailurePercent(100);

    final CountDownLatch latch = new CountDownLatch(1);
    presenter.bindView(new VersionCheckPresenter.View() {
      @Override public void onVersionCheckFinished() {
        // Should not happen
        latch.countDown();
      }

      @Override public void onUpdatedVersionFound(int oldVersionCode, int updatedVersionCode) {
      }
    });

    presenter.checkForUpdates(MockVersionCheckService.CURRENT_VERSION - 2);
    if (latch.await(5, TimeUnit.SECONDS)) {
      throw new AssertionError("Latch should not have counted down successfully");
    }

    assertEquals(1L, latch.getCount());
  }
}
