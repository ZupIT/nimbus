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

package br.com.zup.nimbus.core.unity.network

import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.ServerDrivenConfig
import br.com.zup.nimbus.core.network.DefaultHttpClient
import br.com.zup.nimbus.core.network.ServerDrivenHttpMethod
import br.com.zup.nimbus.core.network.ServerDrivenRequest
import br.com.zup.nimbus.core.unity.network.mocks.TestCustomHttpClient
import br.com.zup.nimbus.core.unity.network.mocks.httpEngineMock
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultHttpClientTest {
  private val client = DefaultHttpClient(httpEngineMock)

  @Test
  fun `should be able to do an http call`() = runTest {
    val response = client.sendRequest(ServerDrivenRequest("/test", null, null, null))
    assertEquals("Hello World", response.body)
  }

  @Test
  fun `should be able to do an http call with custom headers`() = runTest {
    val headers = mapOf(
      "Authentication" to "Bearer test0123456789test",
      "Custom-Header" to "Custom Value",
    )
    val response = client.sendRequest(ServerDrivenRequest("/test", ServerDrivenHttpMethod.Get, headers, null))
    assertEquals("Hello World", response.body)
    assertContains(response.headers, "Authentication")
    assertContains(response.headers, "Custom-Header")
    assertEquals(response.headers["Authentication"], headers["Authentication"])
    assertEquals(response.headers["Custom-Header"], headers["Custom-Header"])
  }

  @Test
  fun `should be able to do an http call with body`() = runTest {
    val bodyPayload = "Test custom body payload"
    val response = client.sendRequest(ServerDrivenRequest(
      "/test-body",
      ServerDrivenHttpMethod.Post,
      emptyMap(),
      bodyPayload
    ))
    assertEquals(response.body, bodyPayload)
  }

  @Test
  fun `should be able to use a custom http client on the nimbus instance`() = runTest {
    val nimbus = Nimbus(
      ServerDrivenConfig(
        baseUrl = "/",
        platform = "test",
        httpClient = TestCustomHttpClient()
      )
    )

    val response = nimbus.httpClient
      .sendRequest(ServerDrivenRequest("/", null, null, null))
    assertEquals(response.status, TestCustomHttpClient.expectedStatusCode)
    assertEquals(response.body, TestCustomHttpClient.expectedBody)
    assertEquals(response.headers, TestCustomHttpClient.expectedHeaders)
    assertEquals(response.bodyBytes, TestCustomHttpClient.expectedBodyBytes)
  }
}
