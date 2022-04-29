package com.zup.nimbus.core.network

import com.zup.nimbus.core.utils.then
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*

class DefaultHttpClient: HttpClient {
  private val client = HttpClient()

  override suspend fun sendRequest(request: NimbusRequest): NimbusResponse {
    val response = doRequest(request)
    return NimbusResponse(
      response.status.value,
      response.bodyAsText(),
      buildResponseHeaders(response.headers),
      response.body()
    )
  }

  private suspend fun doRequest(request: NimbusRequest): HttpResponse {
    val nimbusMethod = (request.method != null) then request.method ?: NimbusHttpMethod.Get
    return client.request(request.url) {
      method = HttpMethod.parse(nimbusMethod.name.uppercase())
      headers {
        request.headers?.entries?.forEach {
          append(it.key, it.value)
        }
      }
      if (bodyIsRequired(nimbusMethod) && request.body != null) {
        setBody(request.body)
      }
    }
  }

  private fun bodyIsRequired(method: NimbusHttpMethod): Boolean {
    return (method == NimbusHttpMethod.Post || method == NimbusHttpMethod.Put || method == NimbusHttpMethod.Patch)
  }

  private fun buildResponseHeaders(headers: Headers): Map<String, String> {
    val responseHeaders = emptyMap<String, String>().toMutableMap()
    headers.entries().map {
      responseHeaders[it.key] = (it.value.size > 1) then it.value.joinToString("; ") ?: it.value[0]
    }
    return responseHeaders
  }
}
