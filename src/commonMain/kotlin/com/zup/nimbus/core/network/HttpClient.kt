package com.zup.nimbus.core.network

const val FIRST_BAD_STATUS = 400

interface HttpClient {
  suspend fun sendRequest(request: ServerDrivenRequest): ServerDrivenResponse
}

enum class ServerDrivenHttpMethod {
  Post,
  Get,
  Put,
  Patch,
  Delete,
}

class ServerDrivenRequest(
  val url: String,
  val method: ServerDrivenHttpMethod? = ServerDrivenHttpMethod.Get,
  val headers: Map<String, String>? = emptyMap(),
  val body: String? = null,
)

class ServerDrivenResponse(
  val status: Int,
  val body: String,
  val headers: Map<String, String>,
  val bodyBytes: ByteArray,
)
