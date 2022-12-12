/*
 * Copyright 2023 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
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
 */

package br.com.zup.nimbus.core.network

import br.com.zup.nimbus.core.utils.then
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpMethod
import kotlin.collections.set

class DefaultHttpClient (engine: HttpClientEngine? = null): br.com.zup.nimbus.core.network.HttpClient {
  constructor() : this(null)

  private val client = if (engine == null) HttpClient() else HttpClient(engine)

  override suspend fun sendRequest(request: ServerDrivenRequest): ServerDrivenResponse {
    val response = doRequest(request)
    return ServerDrivenResponse(
      response.status.value,
      runCatching { response.bodyAsText() }.getOrDefault(""),
      buildResponseHeaders(response.headers),
      response.body()
    )
  }

  private suspend fun doRequest(request: ServerDrivenRequest): HttpResponse {
    val nimbusMethod = ((request.method != null) then request.method) ?: ServerDrivenHttpMethod.Get
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
      responseHeaders[it.key] = ((it.value.size > 1) then it.value.joinToString("; ")) ?: it.value[0]
    }
    return responseHeaders
  }
}
