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
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.PYDroidModule;
import java.util.List;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.Checkout;

public class SupportModule {

  @NonNull private final SupportInteractor interactor;
  @NonNull private final SupportPresenter presenter;

  public SupportModule(@NonNull PYDroidModule.Provider pyDroidModule, @NonNull Activity activity) {
    interactor = new SupportInteractorImpl(
        CheckoutFactory.create(activity, pyDroidModule.provideBilling(),
            pyDroidModule.provideInAppPurchaseList()));
    presenter = new SupportPresenterImpl(interactor);
  }

  @NonNull @CheckResult public SupportPresenter getPresenter() {
    return presenter;
  }

  @SuppressWarnings("WeakerAccess") static final class CheckoutFactory {
    private CheckoutFactory() {
      throw new RuntimeException("No instances");
    }

    @CheckResult @NonNull
    static ICheckout create(@NonNull Activity activity, @NonNull Billing billing,
        @NonNull List<String> inAppPurchaseList) {
      final ActivityCheckout checkout = Checkout.forActivity(activity, billing);
      return new RealCheckout(checkout, inAppPurchaseList);
    }
  }
}
