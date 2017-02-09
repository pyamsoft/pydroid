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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.pyamsoft.pydroid.AboutLibrariesProvider;
import com.pyamsoft.pydroid.SingleInitContentProvider;
import com.pyamsoft.pydroid.about.Licenses;
import com.pyamsoft.pydroid.ui.app.fragment.ActionBarFragment;
import com.pyamsoft.pydroid.ui.databinding.FragmentAboutLibrariesBinding;
import com.pyamsoft.pydroid.util.CircularRevealFragmentUtil;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

public class AboutLibrariesFragment extends ActionBarFragment {

  @NonNull public static final String TAG = "AboutLibrariesFragment";
  @NonNull private static final String KEY_BACK_STACK = "key_back_stack";
  @SuppressWarnings("WeakerAccess") AboutLibrariesPresenter presenter;
  @SuppressWarnings("WeakerAccess") FastItemAdapter<AboutAdapterItem> fastItemAdapter;
  private boolean lastOnBackStack;
  private FragmentAboutLibrariesBinding binding;

  public static void show(@NonNull FragmentActivity activity, @IdRes int containerResId,
      @NonNull BackStackState backStackState) {
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

    presenter = SingleInitContentProvider.getInstance()
        .getModule()
        .provideAboutLibrariesModule()
        .getPresenter();
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
    CircularRevealFragmentUtil.runCircularRevealOnViewCreated(view, getArguments());
    fastItemAdapter.withSelectable(true);

    binding.recyclerAboutLibraries.setLayoutManager(new LinearLayoutManager(getContext()));
    binding.recyclerAboutLibraries.setAdapter(fastItemAdapter);

    fastItemAdapter.withOnBindViewHolderListener(new FastAdapter.OnBindViewHolderListener() {

      @CheckResult @NonNull
      private AboutAdapterItem.ViewHolder toViewHolder(RecyclerView.ViewHolder holder) {
        if (holder instanceof AboutAdapterItem.ViewHolder) {
          return (AboutAdapterItem.ViewHolder) holder;
        } else {
          throw new IllegalStateException("ViewHolder is not AboutAdapterItem.ViewHolder");
        }
      }

      @Override public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position,
          List<Object> payloads) {
        final AboutAdapterItem.ViewHolder holder = toViewHolder(viewHolder);
        final AboutAdapterItem aboutItem =
            fastItemAdapter.getAdapterItem(holder.getAdapterPosition());
        aboutItem.bindView(holder, payloads);
        holder.bind(item -> presenter.loadLicenseText(holder.getAdapterPosition(), item,
            new AboutLibrariesPresenter.LicenseTextLoadCallback() {
              @Override public void onLicenseTextLoadComplete(int position, @NonNull String text) {
                fastItemAdapter.getAdapterItem(position).setLicenseText(text);
                fastItemAdapter.notifyItemChanged(position);
              }

              @Override public void onLicenseTextLoadError(int position) {
                // TODO handle error
              }
            }));
      }

      @Override public void unBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final AboutAdapterItem.ViewHolder holder = toViewHolder(viewHolder);
        final AboutAdapterItem item = (AboutAdapterItem) holder.itemView.getTag();
        if (item != null) {
          item.unbindView(holder);
        }
      }
    });

    final List<AboutAdapterItem> items = new ArrayList<>();
    Licenses.forEach(aboutLicenseItem -> {
      final boolean add;
      add = !Licenses.Names.GOOGLE_PLAY.equals(aboutLicenseItem.name())
          || AboutLibrariesProvider.hasGooglePlayServices(getContext());

      if (add) {
        items.add(new AboutAdapterItem(aboutLicenseItem));
      }
    });

    fastItemAdapter.add(items);
  }

  @Override public void onResume() {
    super.onResume();
    setActionBarUpEnabled(true);
  }

  @Override public void onStart() {
    super.onStart();
    presenter.bindView(null);
  }

  @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
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
