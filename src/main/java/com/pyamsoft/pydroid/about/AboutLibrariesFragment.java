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

package com.pyamsoft.pydroid.about;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.app.fragment.ActionBarFragment;
import java.util.ArrayList;
import java.util.List;

public class AboutLibrariesFragment extends ActionBarFragment {

  @NonNull public static final String TAG = "AboutLibrariesFragment";
  @NonNull private static final String KEY_LICENSE_LIST = "key_license_list";
  @NonNull private static final String KEY_STYLING = "key_styling";
  private FastItemAdapter<ExpandableAboutItem> fastItemAdapter;
  private Licenses[] licenses;
  private RecyclerView recyclerView;
  @ColorInt private int backgroundColor;

  public static void show(@NonNull FragmentActivity activity, @IdRes int containerResId,
      @NonNull Styling styling, @NonNull Licenses... licenses) {
    final FragmentManager fragmentManager = activity.getSupportFragmentManager();
    if (fragmentManager.findFragmentByTag(TAG) == null) {
      fragmentManager.beginTransaction()
          .replace(containerResId, AboutLibrariesFragment.newInstance(styling, licenses), TAG)
          .addToBackStack(TAG)
          .commit();
    }
  }

  @CheckResult @NonNull private static AboutLibrariesFragment newInstance(@NonNull Styling styling,
      @NonNull Licenses... licenseList) {
    final Bundle args = new Bundle();
    final AboutLibrariesFragment fragment = new AboutLibrariesFragment();
    final String[] licenseNames = new String[licenseList.length];
    for (int i = 0; i < licenseNames.length; ++i) {
      licenseNames[i] = licenseList[i].name();
    }

    args.putString(KEY_STYLING, styling.name());
    args.putStringArray(KEY_LICENSE_LIST, licenseNames);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final String[] licenseNames = getArguments().getStringArray(KEY_LICENSE_LIST);
    if (licenseNames == null) {
      throw new RuntimeException("No licenses specified");
    }

    licenses = new Licenses[licenseNames.length];

    final int size = licenses.length;
    for (int i = 0; i < size; ++i) {
      licenses[i] = Licenses.valueOf(licenseNames[i]);
    }

    final String stylingName = getArguments().getString(KEY_STYLING, null);
    if (stylingName == null) {
      throw new RuntimeException("Styling is NULL");
    }

    final Styling styling = Styling.valueOf(stylingName);
    switch (styling) {
      case LIGHT:
        backgroundColor = Color.parseColor("#f9f9f9");
        break;
      case DARK:
        // TODO Figure out material dark default background
        backgroundColor = Color.parseColor("#1d1d1d");
        break;
      default:
        throw new RuntimeException("Invalid styling: " + stylingName);
    }
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_about_libraries, container, false);
    recyclerView = (RecyclerView) view.findViewById(R.id.recycler_about_libraries);
    view.setBackgroundColor(backgroundColor);
    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    fastItemAdapter = new FastItemAdapter<>();
    fastItemAdapter.withSelectable(true);

    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setItemAnimator(new SlideDownAlphaAnimator());
    recyclerView.setAdapter(fastItemAdapter);

    //fill with some sample data
    final List<ExpandableAboutItem> items = new ArrayList<>();
    for (final Licenses license : licenses) {
      final ExpandableAboutItem item;
      switch (license) {
        case ANDROID:
          item = AboutItemsUtil.licenseForAndroid();
          break;
        default:
          throw new RuntimeException("Invalid license: " + license.name());
      }

      items.add(item);
    }
    fastItemAdapter.add(items);

    //restore selections (this has to be done after the items were added
    fastItemAdapter.withSavedInstanceState(savedInstanceState);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    outState = fastItemAdapter.saveInstanceState(outState);
    super.onSaveInstanceState(outState);
  }

  @Override public void onResume() {
    super.onResume();
    setActionBarUpEnabled(true);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    licenses = null;
  }

  public enum Styling {
    LIGHT, DARK
  }
}
