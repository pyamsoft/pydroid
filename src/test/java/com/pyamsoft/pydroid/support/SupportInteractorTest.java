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
 */

package com.pyamsoft.pydroid.support;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import com.pyamsoft.pydroid.BuildConfig;
import com.pyamsoft.pydroid.TestUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class, sdk = 23)
public class SupportInteractorTest {

  private SupportInteractorImpl interactor;

  @Before public void setup() {
    final ActivityController<AppCompatActivity> activityController =
        TestUtils.getAppCompatActivityController();
    // Create the activity because that is the earliest point of possible Interactor entry
    final Activity activity = activityController.create().get();
  }

}
