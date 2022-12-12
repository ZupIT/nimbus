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

package br.com.zup.nimbus.core.unity.network.mocks

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.content.TextContent
import io.ktor.http.HttpStatusCode

val httpEngineMock = MockEngine { request ->
  return@MockEngine when(request.url.toString()) {
    "http://localhost/test" -> respond(
      content = "Hello World",
      status = HttpStatusCode.OK,
      headers = request.headers
    )
    "http://localhost/test-body" -> respond(
      content = (request.body as TextContent).text,
      status = HttpStatusCode.OK,
      headers = request.headers,
    )
    else -> respond(
      content = "Not Found",
      status = HttpStatusCode.NotFound,
    )
  }
}
