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

package com.pyamsoft.pydroid.ui.helper

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.support.annotation.CheckResult
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentManager.FragmentLifecycleCallbacks
import android.view.View
import timber.log.Timber

class Poster private constructor(private val tag: String) {

    var isDestroyed = false
        private set
        @get:CheckResult get

    inline fun <T : View> post(view: T, crossinline func: (T) -> Unit) {
        if (!isDestroyed) {
            view.post {
                if (!isDestroyed) {
                    func(view)
                }
            }
        }
    }

    inline fun <T : View> postDelayed(view: T, delay: Long, crossinline func: (T) -> Unit) {
        if (!isDestroyed) {
            view.postDelayed({
                if (!isDestroyed) {
                    func(view)
                }
            }, delay)
        }
    }

    inline fun <T : View> postOnAnimation(view: T, crossinline func: (T) -> Unit) {
        if (!isDestroyed) {
            view.postOnAnimation {
                if (!isDestroyed) {
                    func(view)
                }
            }
        }
    }

    inline fun <T : View> postOnAnimationDelayed(view: T, delay: Long,
            crossinline func: (T) -> Unit) {
        if (!isDestroyed) {
            view.postOnAnimationDelayed({
                if (!isDestroyed) {
                    func(view)
                }
            }, delay)
        }
    }

    private fun destroy() {
        isDestroyed = true
        Timber.d("Poster destroyed: $tag")
    }

    companion object {

        @JvmStatic
        @CheckResult
        fun install(activity: Activity): Poster {
            val poster = Poster(activity.javaClass.name)
            activity.application.registerActivityLifecycleCallbacks(
                    object : ActivityLifecycleCallbacks {
                        override fun onActivityPaused(p0: Activity?) {
                        }

                        override fun onActivityResumed(p0: Activity?) {
                        }

                        override fun onActivityStarted(p0: Activity?) {
                        }

                        override fun onActivityDestroyed(a: Activity) {
                            if (activity == a) {
                                poster.destroy()
                            }
                        }

                        override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {
                        }

                        override fun onActivityStopped(p0: Activity?) {
                        }

                        override fun onActivityCreated(p0: Activity?, p1: Bundle?) {
                        }

                    })
            return poster
        }

        @JvmStatic
        @CheckResult
        fun install(fragment: Fragment): Poster {
            val poster = Poster(fragment.javaClass.name)
            val fm = fragment.fragmentManager
            if (fm == null) {
                Timber.w(
                        "Can't install a poster to an already dead Fragment: ${fragment.javaClass.name}")
                poster.destroy()
            } else {
                fm.registerFragmentLifecycleCallbacks(
                        object : FragmentLifecycleCallbacks() {

                            override fun onFragmentViewDestroyed(fm: FragmentManager?,
                                    f: Fragment?) {
                                super.onFragmentViewDestroyed(fm, f)
                                if (fragment == f) {
                                    poster.destroy()
                                }
                            }
                        }, false)
            }
            return poster
        }
    }
}

