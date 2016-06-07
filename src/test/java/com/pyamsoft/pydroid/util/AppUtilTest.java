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

package com.pyamsoft.pydroid.util;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import com.pyamsoft.pydroid.BuildConfig;
import com.pyamsoft.pydroid.behavior.HideScrollFABBehavior;
import com.pyamsoft.pydroid.behavior.IgnoreAppBarLayoutFABBehavior;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

@RunWith(RobolectricGradleTestRunner.class) @Config(constants = BuildConfig.class)
public class AppUtilTest {

  @Test public void test_setupFABBehavior() {
    final ActivityController<AppCompatActivity> activityController =
        TestUtils.getAppCompatActivityController();
    // Create the activity so that we have a floating action button
    final Activity activity = activityController.create().get();
    final FloatingActionButton fab = new FloatingActionButton(activity);

    Assert.assertNotNull(fab);

    // Test behaviors
    // Call should not create layout params
    AppUtil.setupFABBehavior(fab, null);
    Assert.assertNull(fab.getLayoutParams());

    // Call should modify existing params
    fab.setLayoutParams(new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT));
    AppUtil.setupFABBehavior(fab, null);
    Assert.assertNotNull(((CoordinatorLayout.LayoutParams) fab.getLayoutParams()).getBehavior());

    // Call should assign custom behavior, and should not create duplicates
    FloatingActionButton.Behavior behavior = new HideScrollFABBehavior();
    AppUtil.setupFABBehavior(fab, behavior);
    Assert.assertNotNull(((CoordinatorLayout.LayoutParams) fab.getLayoutParams()).getBehavior());
    Assert.assertEquals(behavior,
        ((CoordinatorLayout.LayoutParams) fab.getLayoutParams()).getBehavior());

    // The same with a different custom behavior
    behavior = new IgnoreAppBarLayoutFABBehavior();
    AppUtil.setupFABBehavior(fab, behavior);
    Assert.assertNotNull(((CoordinatorLayout.LayoutParams) fab.getLayoutParams()).getBehavior());
    Assert.assertEquals(behavior,
        ((CoordinatorLayout.LayoutParams) fab.getLayoutParams()).getBehavior());

    // Two behaviors are not the same
    AppUtil.setupFABBehavior(fab, null);
    Assert.assertNotNull(((CoordinatorLayout.LayoutParams) fab.getLayoutParams()).getBehavior());
    Assert.assertNotSame(behavior,
        ((CoordinatorLayout.LayoutParams) fab.getLayoutParams()).getBehavior());
    Assert.assertNotSame(new FloatingActionButton.Behavior(),
        ((CoordinatorLayout.LayoutParams) fab.getLayoutParams()).getBehavior());
  }

  @Test public void test_convertToDp() {
    final Context context = RuntimeEnvironment.application.getApplicationContext();

    // Zero test
    final float zero = 0F;
    Assert.assertEquals(zero, AppUtil.convertToDP(context, 0));

    // Reproducibility test
    final float oneDp = AppUtil.convertToDP(context, 1);
    Assert.assertNotSame(oneDp, zero);
    Assert.assertEquals(oneDp, AppUtil.convertToDP(context, 1));

    // Correct logic
    Assert.assertNotSame(oneDp * 2, zero);
    Assert.assertEquals(oneDp * 2, AppUtil.convertToDP(context, 2));

    Assert.assertNotSame(oneDp * 4, zero);
    Assert.assertEquals(oneDp * 4, AppUtil.convertToDP(context, 4));

    Assert.assertNotSame(oneDp * 8, zero);
    Assert.assertEquals(oneDp * 8, AppUtil.convertToDP(context, 8));

    Assert.assertNotSame(oneDp * 16, zero);
    Assert.assertEquals(oneDp * 16, AppUtil.convertToDP(context, 16));
  }
}
