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

package com.pyamsoft.pydroid.cache;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import com.pyamsoft.pydroid.BuildConfig;
import com.pyamsoft.pydroid.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class)
public class PersistentCacheTest {

  @Test public void testSimpleLoad() {
    final ActivityController<AppCompatActivity> controller =
        TestUtils.getAppCompatActivityController();
    final FragmentActivity activity = controller.create().get();

    final Object obj = PersistentCache.load(activity, "KEY", Object::new);
    assertNotNull(obj);

    final Object sameObj = PersistentCache.load(activity, "KEY", () -> {
      throw new IllegalStateException("Should not be called");
    });

    assertNotNull(sameObj);
    assertEquals(obj, sameObj);
  }
}
