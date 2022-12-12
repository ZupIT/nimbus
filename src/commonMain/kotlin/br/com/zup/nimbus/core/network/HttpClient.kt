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
