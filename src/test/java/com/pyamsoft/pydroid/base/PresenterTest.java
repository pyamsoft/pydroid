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

package com.pyamsoft.pydroid.base;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PresenterTest {

  @Rule public final ExpectedException doubleUseException = ExpectedException.none();
  @Rule public final ExpectedException useBeforeCreateException = ExpectedException.none();

  @Test public void test_constructor() {
    final PresenterImpl presenter = new PresenterImpl() {
    };

    // By default, throw if not created
    useBeforeCreateException.expect(IllegalStateException.class);
    Assert.assertNotNull(presenter.getView());
  }

  @Test public void test_onCreateView() {
    final PresenterImpl<String> presenter = new PresenterImpl<String>() {
    };

    // By default, constructed with a null view
    final String hold = "String";
    presenter.bindView(hold);
    Assert.assertNotNull(presenter.getView());

    // Expect an error when create is called again without destroy
    doubleUseException.expect(IllegalStateException.class);
    presenter.bindView(hold);
  }

  @Test public void test_onDestroyView() {
    final PresenterImpl<String> presenter = new PresenterImpl<String>() {
    };

    // By default, constructed with a null view
    final String hold = "String";
    presenter.bindView(hold);
    Assert.assertNotNull(presenter.getView());

    // Expect proper clean up
    presenter.unbindView();

    doubleUseException.expect(IllegalStateException.class);
    presenter.unbindView();
  }
}
