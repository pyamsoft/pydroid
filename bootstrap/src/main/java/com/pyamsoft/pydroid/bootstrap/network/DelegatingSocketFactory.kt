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

package com.pyamsoft.pydroid.bootstrap.network

import android.net.TrafficStats
import androidx.annotation.CheckResult
import java.net.InetAddress
import java.net.Socket
import javax.net.SocketFactory

/** Delegates which tags sockets for Android O Strict-Mode compatibility */
public open class DelegatingSocketFactory
protected constructor(private val delegate: SocketFactory) : SocketFactory() {

  /** See [SocketFactory.createSocket] */
  final override fun createSocket(): Socket {
    return configureSocket(delegate.createSocket())
  }

  /** See [SocketFactory.createSocket] */
  final override fun createSocket(host: String?, port: Int): Socket {
    return configureSocket(delegate.createSocket(host, port))
  }

  /** See [SocketFactory.createSocket] */
  final override fun createSocket(
      host: String?,
      port: Int,
      localAddress: InetAddress?,
      localPort: Int
  ): Socket {
    return configureSocket(delegate.createSocket(host, port, localAddress, localPort))
  }

  /** See [SocketFactory.createSocket] */
  final override fun createSocket(host: InetAddress?, port: Int): Socket {
    return configureSocket(delegate.createSocket(host, port))
  }

  /** See [SocketFactory.createSocket] */
  final override fun createSocket(
      host: InetAddress?,
      port: Int,
      localAddress: InetAddress?,
      localPort: Int
  ): Socket {
    return configureSocket(delegate.createSocket(host, port, localAddress, localPort))
  }

  /** Tag each socket with traffic stats for StrictMode compliance on Android O */
  @CheckResult
  protected open fun configureSocket(socket: Socket): Socket {
    // On Android O and above, StrictMode causes untagged socket errors
    // Setting the ThreadStatsTag seems to fix it
    TrafficStats.setThreadStatsTag(1)

    return socket
  }

  public companion object {

    /** Create a new socket factory */
    @JvmStatic
    @CheckResult
    @JvmOverloads
    public fun create(factory: SocketFactory = getDefault()): SocketFactory {
      return DelegatingSocketFactory(factory)
    }
  }
}
