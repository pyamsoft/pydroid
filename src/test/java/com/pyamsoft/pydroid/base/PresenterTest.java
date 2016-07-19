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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PresenterTest {

  @Rule public final ExpectedException doubleUseException = ExpectedException.none();
  @Rule public final ExpectedException useBeforeCreateException = ExpectedException.none();

  @Test public void test_constructor() {
    final Presenter<String> presenter = new TestPresenter();

    // By default, throw if not created
    useBeforeCreateException.expect(IllegalStateException.class);
    Assert.assertNotNull(presenter.getView());
  }

  @Test public void test_bindView() {
    final Presenter<String> presenter = new TestPresenter();

    // By default, constructed with a null view
    final String hold = "String";
    presenter.bindView(hold);
    Assert.assertNotNull(presenter.getView());

    // Expect an error when create is called again without destroy
    doubleUseException.expect(IllegalStateException.class);
    presenter.bindView(hold);
  }

  @Test public void test_unbindView() {
    final Presenter<String> presenter = new TestPresenter();

    // By default, constructed with a null view
    final String hold = "String";
    presenter.bindView(hold);
    Assert.assertNotNull(presenter.getView());

    // Expect proper clean up
    presenter.unbindView();

    doubleUseException.expect(IllegalStateException.class);
    presenter.unbindView();
  }

  @Test public void test_onBindNoHook() {
    final TestPresenter presenter = new TestPresenter();

    // By default, constructed with a null view
    final String hold = "String";
    presenter.bindView(hold, false);
    Assert.assertNotNull(presenter.getView());

    // When bind is called, no custom hook
    Assert.assertFalse(presenter.isBound());

    presenter.unbindView();
  }

  @Test public void test_onBindHook() {
    final TestPresenter presenter = new TestPresenter();

    // By default, constructed with a null view
    final String hold = "String";
    presenter.bindView(hold);
    Assert.assertNotNull(presenter.getView());

    // When bind is called, no custom hook
    Assert.assertTrue(presenter.isBound());

    presenter.unbindView();
  }

  @Test public void test_onUnbindNoHook() {
    final TestPresenter presenter = new TestPresenter();

    // By default, constructed with a null view
    final String hold = "String";
    presenter.bindView(hold);
    Assert.assertNotNull(presenter.getView());

    presenter.unbindView(false);
    Assert.assertFalse(presenter.isUnbound());
  }

  @Test public void test_onUnbindHook() {
    final TestPresenter presenter = new TestPresenter();

    // By default, constructed with a null view
    final String hold = "String";
    presenter.bindView(hold);
    Assert.assertNotNull(presenter.getView());

    presenter.unbindView();
    Assert.assertTrue(presenter.isUnbound());
  }

  static final class TestPresenter extends Presenter<String> {

    private boolean bound = false;
    private boolean unbound = false;

    @CheckResult public final boolean isBound() {
      return bound;
    }

    @CheckResult public final boolean isUnbound() {
      return unbound;
    }

    @Override protected void onBind(@NonNull String view) {
      super.onBind(view);
      bound = true;
      //noinspection ResultOfMethodCallIgnored
      view.length();
    }

    @Override protected void onUnbind(@NonNull String view) {
      super.onUnbind(view);
      //noinspection ResultOfMethodCallIgnored
      view.length();
      bound = false;
    }
  }
}
