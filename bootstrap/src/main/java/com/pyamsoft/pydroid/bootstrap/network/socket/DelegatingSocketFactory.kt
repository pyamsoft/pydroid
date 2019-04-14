/*
 * Copyright 2019 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.bootstrap.network.socket

import android.net.TrafficStats
import androidx.annotation.CheckResult
import java.net.InetAddress
import java.net.Socket
import javax.net.SocketFactory

open class DelegatingSocketFactory protected constructor(
  private val delegate: SocketFactory
) : SocketFactory() {

  final override fun createSocket(): Socket {
    return delegate.createSocket()
        .also { configureSocket(it) }
  }

  final override fun createSocket(
    host: String?,
    port: Int
  ): Socket {
    return delegate.createSocket(host, port)
        .also { configureSocket(it) }
  }

  final override fun createSocket(
    host: String?,
    port: Int,
    localAddress: InetAddress?,
    localPort: Int
  ): Socket {
    return delegate.createSocket(host, port, localAddress, localPort)
        .also { configureSocket(it) }
  }

  final override fun createSocket(
    host: InetAddress?,
    port: Int
  ): Socket {
    return delegate.createSocket(host, port)
        .also { configureSocket(it) }
  }

  final override fun createSocket(
    host: InetAddress?,
    port: Int,
    localAddress: InetAddress?,
    localPort: Int
  ): Socket {
    return delegate.createSocket(host, port, localAddress, localPort)
        .also { configureSocket(it) }
  }

  @CheckResult
  protected open fun configureSocket(socket: Socket): Socket {
    // On Android O and above, StrictMode causes untagged socket errors
    // Setting the ThreadStatsTag seems to fix it
    TrafficStats.setThreadStatsTag(1)

    return socket
  }

  companion object {

    @JvmStatic
    @CheckResult
    @JvmOverloads
    fun create(factory: SocketFactory = getDefault()): SocketFactory {
      return DelegatingSocketFactory(factory)
    }
  }
}
