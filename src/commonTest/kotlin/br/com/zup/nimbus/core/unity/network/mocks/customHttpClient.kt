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

import br.com.zup.nimbus.core.network.HttpClient
import br.com.zup.nimbus.core.network.ServerDrivenRequest
import br.com.zup.nimbus.core.network.ServerDrivenResponse

class TestCustomHttpClient: HttpClient {
  companion object Factory {
    const val expectedStatusCode = 666
    const val expectedBody = "Custom Http Client Response"
    val expectedHeaders = emptyMap<String, String>()
    val expectedBodyBytes = ByteArray(0)
  }

  override suspend fun sendRequest(request: ServerDrivenRequest): ServerDrivenResponse {
    return ServerDrivenResponse(expectedStatusCode, expectedBody, expectedHeaders, expectedBodyBytes)
  }
}
