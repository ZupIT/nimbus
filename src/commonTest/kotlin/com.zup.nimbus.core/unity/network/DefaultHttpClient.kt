package com.zup.nimbus.core.unity.network

import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.ServerDrivenConfig
import com.zup.nimbus.core.network.DefaultHttpClient
import com.zup.nimbus.core.network.ServerDrivenHttpMethod
import com.zup.nimbus.core.network.ServerDrivenRequest
import com.zup.nimbus.core.unity.network.mocks.TestCustomHttpClient
import com.zup.nimbus.core.unity.network.mocks.httpEngineMock
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
