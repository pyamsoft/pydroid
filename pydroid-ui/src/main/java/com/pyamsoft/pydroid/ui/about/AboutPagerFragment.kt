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
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.util.NetworkUtil
import kotlinx.android.synthetic.main.fragment_pager_about.about_item_homepage
import kotlinx.android.synthetic.main.fragment_pager_about.about_item_webview

@Suppress("ProtectedInFinal")
class AboutPagerFragment : Fragment() {

  protected lateinit var homepage: String
  private lateinit var license: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    homepage = arguments.getString(KEY_HOMEPAGE, null) ?: throw IllegalStateException(
        "Homepage is NULL")
    license = arguments.getString(KEY_LICENSE, null) ?: throw IllegalStateException(
        "License is NULL")
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater?.inflate(R.layout.fragment_pager_about, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    about_item_homepage.paintFlags = (about_item_homepage.paintFlags or Paint.UNDERLINE_TEXT_FLAG)
    about_item_homepage.text = homepage
    about_item_homepage.setOnClickListener {
      NetworkUtil.newLink(context.applicationContext, homepage)
    }

    about_item_webview.settings.defaultFontSize = 12
    about_item_webview.isVerticalScrollBarEnabled = true
    about_item_webview.loadDataWithBaseURL(null, license, "text/plain", "UTF-8", null)
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

