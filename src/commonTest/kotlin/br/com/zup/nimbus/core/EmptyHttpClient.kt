package br.com.zup.nimbus.core

import br.com.zup.nimbus.core.network.HttpClient
import br.com.zup.nimbus.core.network.ServerDrivenRequest
import br.com.zup.nimbus.core.network.ServerDrivenResponse

object EmptyHttpClient: HttpClient {
  override suspend fun sendRequest(request: ServerDrivenRequest): ServerDrivenResponse {
    return ServerDrivenResponse(status = 200, body = "", headers = emptyMap(), bodyBytes = ByteArray(0))
  }
}
