package com.pyamsoft.pydroid.bootstrap.version.socket

import android.net.TrafficStats
import androidx.annotation.CheckResult
import java.net.InetAddress
import java.net.Socket
import javax.net.SocketFactory

open class DelegatingSocketFactory : SocketFactory() {

  private val delegate = SocketFactory.getDefault()

  override fun createSocket(): Socket {
    return delegate.createSocket()
        .also { configureSocket(it) }
  }

  override fun createSocket(
    host: String?,
    port: Int
  ): Socket {
    return delegate.createSocket(host, port)
        .also { configureSocket(it) }
  }

  override fun createSocket(
    host: String?,
    port: Int,
    localAddress: InetAddress?,
    localPort: Int
  ): Socket {
    return delegate.createSocket(host, port, localAddress, localPort)
        .also { configureSocket(it) }
  }

  override fun createSocket(
    host: InetAddress?,
    port: Int
  ): Socket {
    return delegate.createSocket(host, port)
        .also { configureSocket(it) }
  }

  override fun createSocket(
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

}
