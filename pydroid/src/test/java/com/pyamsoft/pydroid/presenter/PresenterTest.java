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
 *
 */

package com.pyamsoft.pydroid.presenter;

import android.support.annotation.NonNull;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PresenterTest {

  @NonNull private static final String view = "VIEW";
  private TestPresenter presenter;

  @Before public void setup() {
    presenter = new TestPresenter();
  }

  @Test public void testCreation() {
    // Before bound, is bound should return false
    assertFalse(presenter.isBound());
  }

  @Test public void testBind() {
    // Before bound, is bound should return false
    assertFalse(presenter.isBound());

    // When bound, the presenter should state so
    presenter.bindView(view);
    assertTrue(presenter.isBound());
  }

  @Test public void testBoundViewRetrieval() {
    testBind();

    // Make sure that when a presenter gets the view it has not been modified
    presenter.getView(s -> assertEquals(view, s));
  }

  @Test public void testUnbind() {
    testBind();

    // Make sure that when unbound, accurately reflect so
    presenter.unbindView();
    assertFalse(presenter.isBound());
  }

  @Test public void testDestroy() {
    testUnbind();

    // Make sure that the destroyed presenter reflects state
    presenter.destroy();
    assertTrue(presenter.isDestroyed());
  }

  @Test public void testUnboundViewRetrieval() {
    assertFalse(presenter.isBound());

    // Throw when there is no view
    presenter.getView(s -> {
      throw new AssertionError("Should not be called");
    });
  }

  @Test public void testMultibind() {
    testBind();

    presenter.bindView(view);
    assertTrue(presenter.isBound());
  }

  @Test public void testMultiunbind() {
    testUnbind();

    presenter.unbindView();
    assertFalse(presenter.isBound());
  }

  private static class TestPresenter extends PresenterBase<String> {

    private boolean destroyed;

    TestPresenter() {
      this.destroyed = false;
    }

    boolean isDestroyed() {
      return destroyed;
    }

    @Override protected void onDestroy() {
      super.onDestroy();
      destroyed = true;
    }
  }
}
