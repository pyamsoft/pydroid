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

package com.pyamsoft.pydroidui.rating;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.text.Spannable;
import com.pyamsoft.pydroid.util.StringUtil;
import com.pyamsoft.pydroidui.donate.DonationActivity;

public abstract class RatingActivity extends DonationActivity
    implements RatingDialog.ChangeLogProvider {

  @NonNull @Override public final Spannable getChangeLogText() {
    // The changelog text
    final String title = "What's New in Version " + getVersionName();
    final String[] lines = getChangeLogLines();

    // Turn it into a spannable
    final String[] fullLines = new String[lines.length + 1];
    fullLines[0] = title;
    System.arraycopy(lines, 0, fullLines, 1, fullLines.length - 1);
    final Spannable spannable = StringUtil.createLineBreakBuilder(fullLines);

    int start = 0;
    int end = title.length();
    final int largeSize =
        StringUtil.getTextSizeFromAppearance(this, android.R.attr.textAppearanceLarge);
    final int largeColor =
        StringUtil.getTextColorFromAppearance(this, android.R.attr.textAppearanceLarge);
    final int smallSize =
        StringUtil.getTextSizeFromAppearance(this, android.R.attr.textAppearanceSmall);
    final int smallColor =
        StringUtil.getTextColorFromAppearance(this, android.R.attr.textAppearanceSmall);

    StringUtil.boldSpan(spannable, start, end);
    StringUtil.sizeSpan(spannable, start, end, largeSize);
    StringUtil.colorSpan(spannable, start, end, largeColor);

    start += end + 2;
    for (final String line : lines) {
      end += 2 + line.length();
    }

    StringUtil.sizeSpan(spannable, start, end, smallSize);
    StringUtil.colorSpan(spannable, start, end, smallColor);

    return spannable;
  }

  @CheckResult @NonNull protected abstract String[] getChangeLogLines();

  @CheckResult @NonNull protected abstract String getVersionName();
}
