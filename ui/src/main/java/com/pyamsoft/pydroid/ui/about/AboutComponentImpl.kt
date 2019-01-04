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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.about.AboutModule
import com.pyamsoft.pydroid.loader.ImageLoader

internal class AboutComponentImpl(
  private val aboutModule: AboutModule,
  private val imageLoader: ImageLoader,
  private val owner: LifecycleOwner,
  private val activity: FragmentActivity,
  private val inflater: LayoutInflater,
  private val container: ViewGroup?,
  savedInstanceState: Bundle?
) : AboutComponent {

  private val aboutView by lazy {
    AboutViewImpl(inflater, container, savedInstanceState, activity, owner)
  }

  override fun inject(fragment: AboutFragment) {
    fragment.rootView = aboutView
    fragment.viewModel = aboutModule.getViewModel()
  }

}
