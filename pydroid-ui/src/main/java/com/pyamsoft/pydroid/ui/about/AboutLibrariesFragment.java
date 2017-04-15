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

package com.pyamsoft.pydroid.ui.about;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.pyamsoft.pydroid.about.AboutLibrariesPresenter;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.ui.PYDroidInjector;
import com.pyamsoft.pydroid.ui.app.fragment.ActionBarFragment;
import com.pyamsoft.pydroid.ui.databinding.FragmentAboutLibrariesBinding;
import java.util.List;
import timber.log.Timber;

public class AboutLibrariesFragment extends ActionBarFragment {

  @NonNull public static final String TAG = "AboutLibrariesFragment";
  @NonNull private static final String KEY_BACK_STACK = "key_back_stack";
  @SuppressWarnings("WeakerAccess") FastItemAdapter<AboutLibrariesItem> fastItemAdapter;
  AboutLibrariesPresenter presenter;
  private boolean lastOnBackStack;
  private FragmentAboutLibrariesBinding binding;

  public static void show(@NonNull FragmentActivity activity, @IdRes int containerResId,
      @NonNull BackStackState backStackState) {
    activity = Checker.checkNonNull(activity);
    backStackState = Checker.checkNonNull(backStackState);

    final FragmentManager fragmentManager = activity.getSupportFragmentManager();
    if (fragmentManager.findFragmentByTag(TAG) == null) {
      fragmentManager.beginTransaction()
          .replace(containerResId, AboutLibrariesFragment.newInstance(backStackState), TAG)
          .addToBackStack(null)
          .commit();
    }
  }

  @CheckResult @NonNull
  private static AboutLibrariesFragment newInstance(@NonNull BackStackState backStackState) {
    final Bundle args = new Bundle();
    final AboutLibrariesFragment fragment = new AboutLibrariesFragment();

    args.putString(KEY_BACK_STACK, backStackState.name());
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final String backStackStateName = getArguments().getString(KEY_BACK_STACK, null);
    if (backStackStateName == null) {
      throw new NullPointerException("BackStateState is NULL");
    }

    final BackStackState backStackState = BackStackState.valueOf(backStackStateName);
    switch (backStackState) {
      case LAST:
        lastOnBackStack = true;
        break;
      case NOT_LAST:
        lastOnBackStack = false;
        break;
      default:
        throw new RuntimeException("Invalid back stack state: " + backStackStateName);
    }

    PYDroidInjector.get().provideComponent().plusAboutLibrariesComponent().inject(this);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    fastItemAdapter = new FastItemAdapter<>();
    binding = FragmentAboutLibrariesBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    fastItemAdapter.withSelectable(true);

    LinearLayoutManager manager = new LinearLayoutManager(getContext());
    manager.setItemPrefetchEnabled(true);
    manager.setInitialPrefetchItemCount(3);
    binding.recyclerAboutLibraries.setLayoutManager(manager);
    binding.recyclerAboutLibraries.setClipToPadding(false);
    binding.recyclerAboutLibraries.setHasFixedSize(true);
    binding.recyclerAboutLibraries.setAdapter(fastItemAdapter);
  }

  @Override public void onStart() {
    super.onStart();
    presenter.loadLicenses(model -> {
      boolean alreadyHas = false;
      List<AboutLibrariesItem> items = fastItemAdapter.getAdapterItems();
      for (AboutLibrariesItem item : items) {
        if (item.getModel() == model) {
          alreadyHas = true;
          break;
        }
      }

      if (!alreadyHas) {
        if (items.isEmpty()) {
          Timber.d("Adding first Library item, hide loading spinner");
          binding.aboutLoading.setVisibility(View.GONE);
          binding.recyclerAboutLibraries.setVisibility(View.VISIBLE);
        }

        binding.recyclerAboutLibraries.post(
            () -> fastItemAdapter.add(new AboutLibrariesItem(model)));
      }
    });
  }

  @Override public void onStop() {
    super.onStop();
    presenter.stop();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    presenter.destroy();
  }

  @Override public void onResume() {
    super.onResume();
    setActionBarUpEnabled(true);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    binding.unbind();
    if (lastOnBackStack) {
      Timber.d("About is last on backstack, set up false");
      setActionBarUpEnabled(false);
    }
  }

  public enum BackStackState {
    LAST, NOT_LAST
  }
}
