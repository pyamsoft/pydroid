/*
 * Copyright 2024 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.ui.util

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.flow.StateFlow

/** Assume not null and remember the result */
@Composable
@CheckResult
public fun <T : Any> rememberNotNull(anything: T?): T {
  return remember(anything) { anything.requireNotNull() }
}

/** Assume not null and remember the result, with custom message */
@Composable
@CheckResult
public fun <T : Any> rememberNotNull(anything: T?, lazyMessage: () -> String): T {
  val handleLazyMessage by rememberUpdatedState(lazyMessage)
  return remember(anything) { anything.requireNotNull(handleLazyMessage) }
}

@CheckResult
private fun resolveActivity(context: Context): ComponentActivity {
  return when (context) {
    is ComponentActivity -> context
    is ContextWrapper -> resolveActivity(context.baseContext)
    else -> {
      Logger.w { "Provided Context is not a ComponentActivity or a ContextWrapper: $context" }
      null
    }
  } ?: throw IllegalStateException("Could not resolve ComponentActivity from Context: $context")
}

/**
 * Resolve the LocalContext as an Activity
 *
 * This should basically always work, given that all ComposeViews are held inside of Activities
 *
 * In the future, if we use Compose in a RemoteView context, this will not work.
 *
 * Callers generally want to use LocalActivity.current unless they have a good reason not to.
 */
@Composable
@CheckResult
public fun rememberActivity(): ComponentActivity {
  val context = LocalContext.current
  return remember(context) { resolveActivity(context) }
}

/**
 * Any list in Compose is unstable and will fire recompositions a ton. Use this helper to turn your
 * data State list into a compose ready list
 */
@Composable
@CheckResult
public fun <T : Any> Collection<T>.rememberAsStateList(): List<T> {
  return remember(this) { this.toMutableStateList() }
}

/** rememberAsStateList convenience for Maps */
@Composable
@CheckResult
public fun <K : Any, V : Any> Map<K, V>.rememberAsStateList(): List<Map.Entry<K, V>> {
  return remember(this) { this.entries.toMutableStateList() }
}

/**
 * Any Map in Compose is unstable and will fire recompositions a ton. Use this helper to turn your
 * data State Map into a compose ready list
 */
@Composable
@CheckResult
public fun <K : Any, V : Any> Map<K, V>.rememberAsStateMap(): SnapshotStateMap<K, V> {
  return remember(this) { mutableStateMapOf(*this.toList().toTypedArray()) }
}

/**
 * Any list in Compose is unstable and will fire recompositions a ton. Use this helper to turn your
 * data State list into a compose ready list
 */
@Composable
@CheckResult
public fun <T : Any> StateFlow<Collection<T>>.collectAsStateList(
    context: CoroutineContext = EmptyCoroutineContext
): List<T> {
  val state by this.collectAsState(context = context)
  return state.rememberAsStateList()
}

/**
 * Any Map in Compose is unstable and will fire recompositions a ton. Use this helper to turn your
 * data State Map into a compose ready list
 */
@Composable
@CheckResult
public fun <K : Any, V : Any> StateFlow<Map<K, V>>.collectAsStateMap(
    context: CoroutineContext = EmptyCoroutineContext
): SnapshotStateMap<K, V> {
  val state by this.collectAsState(context = context)
  return state.rememberAsStateMap()
}

/**
 * Any list in Compose is unstable and will fire recompositions a ton. Use this helper to turn your
 * data State list into a compose ready list
 */
@Composable
@CheckResult
public fun <T : Any> StateFlow<Collection<T>>.collectAsStateListWithLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext
): List<T> {
  val state by
      this.collectAsStateWithLifecycle(
          lifecycleOwner = lifecycleOwner,
          minActiveState = minActiveState,
          context = context,
      )
  return state.rememberAsStateList()
}

/**
 * Any Map in Compose is unstable and will fire recompositions a ton. Use this helper to turn your
 * data State Map into a compose ready list
 */
@Composable
@CheckResult
public fun <K : Any, V : Any> StateFlow<Map<K, V>>.collectAsStateMapWithLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext
): SnapshotStateMap<K, V> {
  val state by
      this.collectAsStateWithLifecycle(
          lifecycleOwner = lifecycleOwner,
          minActiveState = minActiveState,
          context = context,
      )
  return state.rememberAsStateMap()
}
