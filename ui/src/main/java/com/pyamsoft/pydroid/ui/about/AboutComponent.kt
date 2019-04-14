/*
 * Copyright 2019 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.about

import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.ui.about.AboutComponent.AboutModule
import com.pyamsoft.pydroid.ui.app.ToolbarActivity
import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import javax.inject.Named

@Subcomponent(modules = [AboutModule::class])
internal interface AboutComponent {

  fun inject(fragment: AboutFragment)

  @Subcomponent.Factory
  interface Factory {

    @CheckResult
    fun create(
      @BindsInstance owner: LifecycleOwner,
      @BindsInstance toolbarActivity: ToolbarActivity,
      @BindsInstance @Named("backstack") backstack: Int,
      @BindsInstance parent: ViewGroup
    ): AboutComponent

  }

  @Module
  abstract class AboutModule {

    @Binds
    @CheckResult
    internal abstract fun bindUiComponent(impl: AboutUiComponentImpl): AboutUiComponent

    @Binds
    @CheckResult
    internal abstract fun bindUiCallback(impl: AboutHandler): AboutListView.Callback

    @Binds
    @CheckResult
    internal abstract fun bindToolbarComponent(impl: AboutToolbarUiComponentImpl): AboutToolbarUiComponent

    @Binds
    @CheckResult
    internal abstract fun bindToolbarCallback(impl: AboutToolbarHandler): AboutToolbarView.Callback
  }
}
