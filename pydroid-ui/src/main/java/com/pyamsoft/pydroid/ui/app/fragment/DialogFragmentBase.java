package com.pyamsoft.pydroid.ui.app.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Window;

public abstract class DialogFragmentBase extends DialogFragment {

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    if (!hasTitle()) {
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    return dialog;
  }

  @CheckResult protected boolean hasTitle() {
    return false;
  }
}
