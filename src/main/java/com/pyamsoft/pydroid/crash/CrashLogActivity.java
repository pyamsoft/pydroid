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
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.util.IMMLeakUtil;
import com.pyamsoft.pydroid.util.StringUtil;
import java.io.File;
import timber.log.Timber;

@SuppressLint("Registered") public final class CrashLogActivity extends AppCompatActivity {

  public static final String APP_NAME = "APP_NAME";
  public static final String CRASH_EMAIL = "CRASH_EMAIL";
  public static final String CRASH_SUBJECT = "CRASH_SUBJECT";
  public static final String CRASH_TEXT = "CRASH_TEXT";
  public static final String CRASH_FILE = "CRASH_FILE";
  public static final String BUG_REPORT = "BUG_REPORT";
  private static final String[] DEFAULT_EMAIL = { "pyam.soft@gmail.com" };
  private static final String DEFAULT_SUBJECT = "pyamsoft Application Crash Log";
  private static final String DEFAULT_TEXT = "Crash log attached.";

  private static final String[] BUGREPORT_EMAIL = DEFAULT_EMAIL;
  private static final String BUGREPORT_SUBJECT = "pyamsoft Bug Report";
  private static final String BUGREPORT_TEXT = "Please describe your bug report below:";

  // Can't use butterknife in libraries
  private Button sendLog;
  private TextView oopsText;

  private Intent intent;

  private boolean isBugReport;

  @Override public void onCreate(Bundle savedInstanceState) {
    IMMLeakUtil.fixFocusedViewLeak(getApplication());
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_crashlog);

    isBugReport = getIntent().getBooleanExtra(BUG_REPORT, false);

    findViews();
    formatOopsMessage();
    setupMessage();
    setupMailIntent();
    setupSendLogButton();
  }

  private void formatOopsMessage() {
    String appName = getIntent().getStringExtra(APP_NAME);
    if (appName == null) {
      appName = "this pyamsoft Application";
    }

    final String formatted = StringUtil.formatString(oopsText.getText().toString(), appName);
    oopsText.setText(formatted);
  }

  private void setupMessage() {
    if (isBugReport) {
      Timber.w("Exception thrown by User Bug Report");
    } else {
      Timber.w("Uncaught exception");
    }
  }

  private void setupMailIntent() {
    final ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(this)
        .setChooserTitle("Send Email Using:")
        .setType("message/rfc822");

    final Intent passedData = getIntent();
    String[] emails = passedData.getStringArrayExtra(CRASH_EMAIL);
    if (emails == null || emails.length == 0) {
      Timber.w("Setting Default Email");
      emails = (isBugReport ? BUGREPORT_EMAIL : DEFAULT_EMAIL);
    } else {
      for (final String e : emails) {
        Timber.d("Setting emails: %s", e);
      }
    }
    intentBuilder.addEmailTo(emails);

    String subject = passedData.getStringExtra(CRASH_SUBJECT);
    if (subject == null || subject.isEmpty()) {
      Timber.w("Setting Default Subject");
      subject = (isBugReport ? BUGREPORT_SUBJECT : DEFAULT_SUBJECT);
    } else {
      Timber.d("Setting subject: %s", subject);
    }
    intentBuilder.setSubject(subject);

    // do this so some email clients don't complain about empty body.
    String text = passedData.getStringExtra(CRASH_TEXT);
    if (text == null || text.isEmpty()) {
      Timber.w("Setting Default Text");
      text = (isBugReport ? BUGREPORT_TEXT : DEFAULT_TEXT);
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
