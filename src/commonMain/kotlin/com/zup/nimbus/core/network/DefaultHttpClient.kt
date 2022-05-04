package com.zup.nimbus.core.network

import com.zup.nimbus.core.unit.then
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.*
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpMethod
import kotlin.collections.set

class DefaultHttpClient(engine: HttpClientEngine? = null): com.zup.nimbus.core.network.HttpClient {
  private val client = if (engine == null) HttpClient() else HttpClient(engine)

  override suspend fun sendRequest(request: ServerDrivenRequest): ServerDrivenResponse {
    val response = doRequest(request)
    return ServerDrivenResponse(
      response.status.value,
      response.bodyAsText(),
      buildResponseHeaders(response.headers),
      response.body()
    )
  }

  private suspend fun doRequest(request: ServerDrivenRequest): HttpResponse {
    val nimbusMethod = (request.method != null) then request.method ?: ServerDrivenHttpMethod.Get
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

  private fun bodyIsRequired(method: ServerDrivenHttpMethod): Boolean {
    return (
      method == ServerDrivenHttpMethod.Post ||
      method == ServerDrivenHttpMethod.Put ||
      method == ServerDrivenHttpMethod.Patch
    )
  }

  private fun buildResponseHeaders(headers: Headers): Map<String, String> {
    val responseHeaders = emptyMap<String, String>().toMutableMap()
    headers.entries().map {
      responseHeaders[it.key] = (it.value.size > 1) then it.value.joinToString("; ") ?: it.value[0]
    }
    return responseHeaders
  }
}
