package com.pyamsoft.pydroid.base;

import android.support.annotation.NonNull;

public abstract class NoDonationActivityBase extends ActivityBase {

  @Override protected final boolean isDonationSupported() {
    return false;
  }

  @NonNull @Override protected final String getPlayStoreAppPackage() {
    return "";
  }
}
