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

package com.pyamsoft.pydroid.dagger.presenter;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PresenterTest {

  @Rule public final ExpectedException useBeforeCreateException = ExpectedException.none();

  @Test public void test_presenter_lifecycle() {
    final TestPresenter presenter = new TestPresenter();

    // Before bound, is bound should return false
    Assert.assertFalse(presenter.isBound());

    // When bound, the presenter should state so
    final String view = "String";
    presenter.bindView(view);
    Assert.assertTrue(presenter.isBound());

    // Make sure that when a presenter gets the view it has not been modified
    Assert.assertEquals(view, presenter.getView());

    // Make sure that when unbound, accurately reflect so
    presenter.unbindView();
    Assert.assertFalse(presenter.isBound());

    // Throw when there is no view
    useBeforeCreateException.expect(PresenterUnboundException.class);
    Assert.assertNotNull(presenter.getView());

    // Make sure that the destroyed presenter reflects state
    presenter.destroy();
    Assert.assertTrue(presenter.isDestroyed());
  }

  static class TestPresenter extends PresenterBase<String> {

    private boolean destroyed;

    public TestPresenter() {
      this.destroyed = false;
    }

    public boolean isDestroyed() {
      return destroyed;
    }

    @Override protected void onDestroy() {
      super.onDestroy();
      destroyed = true;
    }
  }
}
