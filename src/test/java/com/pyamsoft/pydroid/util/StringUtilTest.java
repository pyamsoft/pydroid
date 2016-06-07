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

import android.text.Spannable;
import com.pyamsoft.pydroid.BuildConfig;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class) @Config(constants = BuildConfig.class)
public class StringUtilTest {

  @Test public void test_createBuilder() {
    String line1 = "line1";
    String line2 = "line2";
    final Spannable strb = StringUtil.createBuilder(line1, line2);

    // Test that the length is the same as the two pieces combined
    Assert.assertEquals(line1.length() + line2.length(), strb.length());
  }

  @Test public void test_createLineBreakBuilder() {
    String line1 = "line1";
    String line2 = "line2";
    final Spannable strb = StringUtil.createLineBreakBuilder(line1, line2);

    // Test that the length is the same as the two pieces combined with line breaks
    Assert.assertEquals(line1.length() + line2.length() + "\n\n".length(), strb.length());
    Assert.assertTrue(strb.charAt(line1.length()) == '\n');
    Assert.assertTrue(strb.charAt(line1.length() + 1) == '\n');
    Assert.assertFalse(strb.charAt(line1.length() + 2) == '\n');
    Assert.assertTrue(strb.charAt(line1.length() + 2) == 'l');
  }
}
