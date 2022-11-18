package com.zup.nimbus.core

import com.zup.nimbus.core.network.HttpClient
import com.zup.nimbus.core.network.ServerDrivenRequest
import com.zup.nimbus.core.network.ServerDrivenResponse

object EmptyHttpClient: HttpClient {
  override suspend fun sendRequest(request: ServerDrivenRequest): ServerDrivenResponse {
    return ServerDrivenResponse(status = 200, body = "", headers = emptyMap(), bodyBytes = ByteArray(0))
  }
}
