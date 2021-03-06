package zio.keeper

import zio.duration.Duration
import zio.keeper.membership.NodeAddress
import zio.nio.core.SocketAddress

sealed abstract class Error(val msg: String = "") {
  override def toString: String = msg
}

sealed abstract class SerializationError(msg: String = "") extends Error(msg = msg)

object SerializationError {

  final case class SerializationTypeError(msg0: String)
      extends SerializationError(
        msg = msg0
      )

  object SerializationTypeError {

    def apply(cause: Throwable): SerializationTypeError =
      SerializationTypeError(s"Cannot serialize because of ${cause.getMessage}")
  }

  final case class DeserializationTypeError(msg0: String)
      extends SerializationError(
        msg = msg0
      )

  object DeserializationTypeError {

    def apply(cause: Throwable): DeserializationTypeError =
      new DeserializationTypeError(s"Cannot deserialize because of ${cause.getMessage}")
  }

}

final case class ServiceDiscoveryError(override val msg: String) extends Error

sealed abstract class ClusterError(msg: String = "") extends Error(msg = msg)

object ClusterError {

  final case class UnknownNode(nodeId: NodeAddress) extends ClusterError(msg = nodeId.toString + " is not in cluster")

}

sealed abstract class TransportError(msg: String = "") extends Error(msg = msg)

object TransportError {

  final case class ExceptionWrapper(throwable: Throwable)
      extends TransportError(msg = if (throwable.getMessage == null) throwable.toString else throwable.getMessage)

  final case class RequestTimeout(addr: SocketAddress, timeout: Duration)
      extends TransportError(msg = s"Request timeout $timeout for connection [$addr].")

  final case class ConnectionTimeout(addr: SocketAddress, timeout: Duration)
      extends TransportError(msg = s"Connection timeout $timeout to [$addr].")

  final case class BindFailed(addr: SocketAddress, exc: Throwable)
      extends TransportError(msg = s"Failed binding to address $addr.")

  final case class ChannelClosed(socketAddress: SocketAddress)
      extends TransportError(msg = s"Channel to $socketAddress is closed")

}
