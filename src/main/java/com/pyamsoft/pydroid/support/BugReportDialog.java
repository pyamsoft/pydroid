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

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import com.pyamsoft.pydroid.crash.BugReportException;

public class BugReportDialog extends DialogFragment {

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new AlertDialog.Builder(getActivity()).setCancelable(true)
        .setTitle("Create Bug Report")
        .setMessage(
            "Create a bug report to send to pyamsoft. Please include specific information in the body of your email.")
        .setPositiveButton("Okay", (dialogInterface, i) -> {
          throw new BugReportException();
        })
        .setNegativeButton("No Thanks", (dialogInterface, i) -> {
          dialogInterface.dismiss();
        })
        .create();
  }
}
