package com.zup.nimbus.core.network

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
  val method: ServerDrivenHttpMethod?,
  val headers: Map<String, String>?,
  val body: String?,
)

class ServerDrivenResponse(
  val status: Int,
  val body: String,
  val headers: Map<String, String>,
  val bodyBytes: ByteArray,
)
