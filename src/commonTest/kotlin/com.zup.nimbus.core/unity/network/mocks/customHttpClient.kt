package com.zup.nimbus.core.unity.network.mocks

import com.zup.nimbus.core.network.ServerDrivenRequest
import com.zup.nimbus.core.network.ServerDrivenResponse

class TestCustomHttpClient: com.zup.nimbus.core.network.HttpClient {
  companion object Factory {
    val expectedStatusCode = 666
    val expectedBody = "Custom Http Client Response"
    val expectedHeaders = emptyMap<String, String>()
    val expectedBodyBytes = ByteArray(0)
  }

  override suspend fun sendRequest(request: ServerDrivenRequest): ServerDrivenResponse {
    return ServerDrivenResponse(expectedStatusCode, expectedBody, expectedHeaders, expectedBodyBytes)
  }
}
