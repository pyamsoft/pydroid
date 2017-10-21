/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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
import com.pyamsoft.pydroid.ui.databinding.FragmentPagerAboutBinding
import com.pyamsoft.pydroid.util.NetworkUtil

class AboutPagerFragment : Fragment() {

  private lateinit var homepage: String
  private lateinit var license: String
  private lateinit var binding: FragmentPagerAboutBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
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


    @CheckResult
    fun newInstance(model: AboutLibrariesModel): AboutPagerFragment {
      val fragment = AboutPagerFragment()
      val args = Bundle()
      args.putString(KEY_HOMEPAGE, model.homepage)
      args.putString(KEY_LICENSE, model.license)
      fragment.arguments = args
      return fragment
    }
  }

}

