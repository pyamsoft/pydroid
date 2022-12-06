/*
 * Copyright 2022 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

/**
 * A simple component that follows the SwipeRefresh migration guide
 *
 * https://google.github.io/accompanist/swiperefresh/#migration
 */
@Composable
@OptIn(ExperimentalMaterialApi::class)
public fun SwipeRefresh(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
  // The callback will be remembered by this
  // The boolean will be remembered by this
  val state =
      rememberPullRefreshState(
          refreshing = isRefreshing,
          onRefresh = onRefresh,
      )

  Box(
      modifier = modifier.pullRefresh(state),
  ) {
    content()

    // Place this after the content so it renders above it
    PullRefreshIndicator(
        modifier = Modifier.align(Alignment.TopCenter),
        refreshing = isRefreshing,
        state = state,
    )
  }
}

@Preview
@Composable
private fun PreviewSwipeRefresh() {
  SwipeRefresh(
      isRefreshing = false,
      onRefresh = {},
  ) {}
}
