package br.com.zup.nimbus.core.network

open class NetworkError(override val message: String): Error(message)

class RequestError(
  message: String?,
): NetworkError("Error while sending the request.${if (message == null) "" else " See the details below:\n$message"}")

class ResponseError(
  val status: Int,
  val body: String?,
): NetworkError("Request failed with status $status.${if (body == null) "" else "\n$body"}")
