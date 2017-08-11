/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.about

import android.graphics.Paint
import android.os.Bundle
import android.support.annotation.CheckResult
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pyamsoft.pydroid.about.AboutLibrariesModel
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.databinding.FragmentPagerAboutBinding
import com.pyamsoft.pydroid.util.NetworkUtil

class AboutPagerFragment : Fragment() {

  private lateinit var homepage: String
  private lateinit var license: String
  private lateinit var binding: FragmentPagerAboutBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    PYDroid.with {
      it.inject(this)
    }

    homepage = arguments.getString(KEY_HOMEPAGE, null) ?: throw IllegalStateException(
        "Homepage is NULL")
    license = arguments.getString(KEY_LICENSE, null) ?: throw IllegalStateException(
        "License is NULL")
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = FragmentPagerAboutBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.aboutItemWebview.settings.defaultFontSize = 12
    binding.aboutItemWebview.isVerticalScrollBarEnabled = true
    binding.aboutItemWebview.loadDataWithBaseURL(null, license, "text/plain", "UTF-8", null)

    binding.aboutItemHomepage.paintFlags = (binding.aboutItemHomepage.paintFlags or Paint.UNDERLINE_TEXT_FLAG)
    binding.aboutItemHomepage.text = homepage
    binding.aboutItemHomepage.setOnClickListener {
      NetworkUtil.newLink(it.context.applicationContext, homepage)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding.aboutItemHomepage.text = null
    binding.aboutItemHomepage.setOnClickListener(null)
    binding.aboutItemWebview.loadDataWithBaseURL(null, null, "text/plain", "UTF-8", null)
  }

  companion object {

    private const val KEY_HOMEPAGE = "key_homepage"
    private const val KEY_LICENSE = "key_license"

    @JvmStatic @CheckResult fun newInstance(model: AboutLibrariesModel): AboutPagerFragment {
      val fragment = AboutPagerFragment()
      val args = Bundle()
      args.putString(KEY_HOMEPAGE, model.homepage)
      args.putString(KEY_LICENSE, model.license)
      fragment.arguments = args
      return fragment
    }
  }

}

