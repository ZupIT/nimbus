package com.zup.nimbus.core.network

interface HttpClient {
  suspend fun sendRequest(request: NimbusRequest): NimbusResponse
}

typealias RequestCallback = (response: NimbusResponse) -> Unit

enum class NimbusHttpMethod {
  Post,
  Get,
  Put,
  Patch,
  Delete
}

class NimbusRequest (
  val url: String,
  val method: NimbusHttpMethod?,
  val headers: Map<String, String>?,
  val body: String?) {
}

class NimbusResponse (
  val status: Int,
  val body: String,
  val headers: Map<String, String>,
  val bodyBytes: ByteArray) {
}
