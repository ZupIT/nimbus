package com.zup.nimbus.core.network

import com.zup.nimbus.core.log.Logger
import com.zup.nimbus.core.tree.IdManager
import com.zup.nimbus.core.tree.RenderNode
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DefaultViewClient(
  private val httpClient: HttpClient,
  private val urlBuilder: UrlBuilder,
  private val idManager: IdManager,
  private val logger: Logger,
  private val platform: String,
) : ViewClient {
  override suspend fun fetch(request: ViewRequest): RenderNode {
    val coreHeaders = mapOf(
      // "Content-Type" to "application/json", fixme: ktor doesn't like this header
      "platform" to platform,
    )
    val url = urlBuilder.build(request.url)
    val response: ServerDrivenResponse
    try {
      try {
        response = httpClient.sendRequest(
          ServerDrivenRequest(
            url = url,
            method = request.method,
            headers = coreHeaders + (request.headers ?: emptyMap()),
            body = if (request.body == null) null else Json.encodeToString(request.body),
          )
        )
      } catch (e: Throwable) {
        throw RequestError(e.message)
      }

      if (response.status < FIRST_BAD_STATUS) return RenderNode.fromJsonString(response.body, idManager)
      throw ResponseError(response.status, response.body)
    } catch (e: Throwable) {
      if (request.fallback == null) throw e
      logger.error("Failed to perform network request to $url, using the provided fallback view instead. " +
        "Cause:\n${e.message ?: "Unknown"}")
      return request.fallback
    }
  }
}
