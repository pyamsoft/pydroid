/*
 * Copyright 2021 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.inject

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.Context
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.Logger.e

/** Injects PYDroid and its managed services into a context */
public object Injector {

  /**
   * Obtain a component from the Application
   *
   * Calls through to Application.getSystemService()
   */
  @JvmStatic
  @CheckResult
  public inline fun <reified T : Any> obtainFromApplication(application: Application): T {
    return obtainFromApplication(application, T::class.java)
  }

  /**
   * Obtain a component from the Application
   *
   * Calls through to Application.getSystemService()
   */
  @JvmStatic
  @CheckResult
  public fun <T : Any> obtainFromApplication(application: Application, targetClass: Class<T>): T {
    return resolve(application, targetClass)
  }

  /**
   * Obtain a component from the Application context
   *
   * Calls through to Application.getSystemService()
   */
  @JvmStatic
  @CheckResult
  public inline fun <reified T : Any> obtainFromApplication(context: Context): T {
    return obtainFromApplication(context, T::class.java)
  }

  /**
   * Obtain a component from the Application context
   *
   * Calls through to Application.getSystemService()
   */
  @JvmStatic
  @CheckResult
  public fun <T : Any> obtainFromApplication(context: Context, targetClass: Class<T>): T {
    return resolve(context.applicationContext, targetClass)
  }

  /**
   * Obtain a component from the Activity
   *
   * Calls through to Activity.getSystemService()
   */
  @JvmStatic
  @CheckResult
  public inline fun <reified T : Any> obtainFromActivity(activity: Activity): T {
    return obtainFromActivity(activity, T::class.java)
  }

  /**
   * Obtain a component from the Activity
   *
   * Calls through to Activity.getSystemService()
   */
  @JvmStatic
  @CheckResult
  public fun <T : Any> obtainFromActivity(activity: Activity, targetClass: Class<T>): T {
    return resolve(activity, targetClass)
  }

  /**
   * Obtain a component from the Service
   *
   * Calls through to Service.getSystemService()
   */
  @JvmStatic
  @CheckResult
  public inline fun <reified T : Any> obtainFromService(service: Service): T {
    return obtainFromService(service, T::class.java)
  }

  /**
   * Obtain a component from the Service
   *
   * Calls through to Service.getSystemService()
   */
  @JvmStatic
  @CheckResult
  public fun <T : Any> obtainFromService(service: Service, targetClass: Class<T>): T {
    return resolve(service, targetClass)
  }

  @JvmStatic
  private fun serviceNotFound(context: Context, name: String): Nothing {
    val error = IllegalArgumentException("Unable to find service: $name in context: $context")
    Logger.e(error, "Service not found")
    throw error
  }

  @JvmStatic
  @CheckResult
  @SuppressLint("WrongConstant")
  private fun <T : Any> resolve(context: Context, targetClass: Class<T>): T {
    val name = targetClass.name
    val service = context.getSystemService(name) ?: serviceNotFound(context, name)

    @Suppress("UNCHECKED_CAST") return service as T
  }
}
