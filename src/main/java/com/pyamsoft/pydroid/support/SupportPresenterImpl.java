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

import android.content.Intent;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.presenter.PresenterBase;

class SupportPresenterImpl extends PresenterBase<SupportPresenter.View>
    implements SupportPresenter {

  SupportPresenterImpl() {
  }

  @Override
  public void processDonationResult(int requestCode, int resultCode, @Nullable Intent data) {
    getView(view -> view.onDonationResult(requestCode, resultCode, data));
  }
}
