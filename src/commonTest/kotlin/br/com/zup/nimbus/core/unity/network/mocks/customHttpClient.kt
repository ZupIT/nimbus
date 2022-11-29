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
