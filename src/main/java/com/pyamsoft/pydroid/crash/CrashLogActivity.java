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

package com.pyamsoft.pydroid.crash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.util.IMMLeakUtil;
import java.io.File;
import timber.log.Timber;

@SuppressLint("Registered") public final class CrashLogActivity extends AppCompatActivity {

  @NonNull public static final String APP_NAME = "APP_NAME";
  @NonNull public static final String CRASH_SUBJECT = "CRASH_SUBJECT";
  @NonNull public static final String CRASH_TEXT = "CRASH_TEXT";
  @NonNull public static final String CRASH_FILE = "CRASH_FILE";
  @NonNull private static final String DEFAULT_SUBJECT = "pyamsoft Application Crash Log";
  @NonNull private static final String DEFAULT_TEXT = "Crash log attached.";

  // Can't use butterknife in libraries
  @Nullable private Button sendLog;
  @Nullable private TextView oopsText;
  @Nullable private Intent intent;

  @Override public void onCreate(Bundle savedInstanceState) {
    IMMLeakUtil.fixFocusedViewLeak(getApplication());
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_crashlog);

    findViews();
    formatOopsMessage();
    setupMailIntent();
    setupSendLogButton();
  }

  private void formatOopsMessage() {
    String appName = getIntent().getStringExtra(APP_NAME);
    if (appName == null) {
      appName = "this pyamsoft Application";
    }

    assert oopsText != null;
    final String formatted = String.format(oopsText.getText().toString(), appName);
    oopsText.setText(formatted);
  }

  private void setupMailIntent() {
    final ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(this)
        .setChooserTitle("Send Email Using:")
        .setType("message/rfc822");

    final Intent passedData = getIntent();
    intentBuilder.addEmailTo(new String[] { "pyam.soft@gmail.com" });

    String subject = passedData.getStringExtra(CRASH_SUBJECT);
    if (subject == null || subject.isEmpty()) {
      Timber.w("Setting Default Subject");
      subject = DEFAULT_SUBJECT;
    } else {
      Timber.d("Setting subject: %s", subject);
    }
    intentBuilder.setSubject(subject);

    // do this so some email clients don't complain about empty body.
    String text = passedData.getStringExtra(CRASH_TEXT);
    if (text == null || text.isEmpty()) {
      Timber.w("Setting Default Text");
      text = DEFAULT_TEXT;
    } else {
      Timber.d("Setting text: %s", text);
    }
    intentBuilder.setText(text);

    // Uri of crash file
    final String filePath = passedData.getStringExtra(CRASH_FILE);
    if (filePath == null) {
      // No file path, can't send log
      Timber.e("Missing File path, can't send log");
      intent = null;
    } else {
      final String packageName = getPackageName();
      final String authority = packageName + ".crash.LOG_PROVIDER";
      final Uri crashUri = FileProvider.getUriForFile(this, authority, new File(filePath));
      Timber.d("Setting stream URI: %s", crashUri);
      intentBuilder.setStream(crashUri);
    }

    // Allow other apps to read the Stream URI
    intent = intentBuilder.createChooserIntent().addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
  }

  private void setupSendLogButton() {
    assert sendLog != null;
    if (intent == null) {
      // No crash log available, no button
      sendLog.setVisibility(View.GONE);
    } else {
      sendLog.setOnClickListener(view -> {
        if (intent == null) {
          Timber.e("Send intent is NULL");
        } else {
          startActivity(intent);
        }

        finish();
      });
    }
  }

  private void findViews() {
    sendLog = (Button) findViewById(R.id.crashlog_open_email);
    oopsText = (TextView) findViewById(R.id.crashlog_oopsie);
  }
}
