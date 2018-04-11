/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.ui.about

import android.graphics.Paint
import android.os.Bundle
import android.support.annotation.CheckResult
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pyamsoft.pydroid.base.about.AboutLibrariesModel
import com.pyamsoft.pydroid.ui.databinding.FragmentPagerAboutBinding
import com.pyamsoft.pydroid.ui.util.navigate
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener
import com.pyamsoft.pydroid.util.hyperlink

internal class AboutPagerFragment : Fragment() {

  private lateinit var homepage: String
  private lateinit var license: String
  private lateinit var binding: FragmentPagerAboutBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments?.also {
      homepage = it.getString(KEY_HOMEPAGE, "")
      license = it.getString(KEY_LICENSE, "")
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentPagerAboutBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    binding.apply {
      aboutItemWebview.settings.defaultFontSize = 12
      aboutItemWebview.isVerticalScrollBarEnabled = true
      aboutItemWebview.loadDataWithBaseURL(null, license, "text/plain", "UTF-8", null)

      aboutItemHomepage.paintFlags = (aboutItemHomepage.paintFlags or Paint.UNDERLINE_TEXT_FLAG)
      aboutItemHomepage.text = homepage
      aboutItemHomepage.setOnDebouncedClickListener {
        homepage.hyperlink(it.context)
            .navigate(requireActivity(), view)
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding.apply {
      aboutItemHomepage.text = null
      aboutItemHomepage.setOnDebouncedClickListener(null)
      aboutItemWebview.loadDataWithBaseURL(null, null, "text/plain", "UTF-8", null)
      unbind()
    }
  }

  companion object {

    private const val KEY_HOMEPAGE = "key_homepage"
    private const val KEY_LICENSE = "key_license"

    @JvmStatic
    @CheckResult
    fun newInstance(model: AboutLibrariesModel): AboutPagerFragment {
      return AboutPagerFragment().apply {
        arguments = Bundle().apply {
          putString(KEY_HOMEPAGE, model.homepage)
          putString(KEY_LICENSE, model.license)
        }
      }
    }
  }
}
