package com.zup.nimbus.core.network

import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.tree.dynamic.node.RootNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DefaultViewClient(val nimbus: Nimbus) : ViewClient {
  // used to prevent the ViewClient from launching multiple requests to the same URL in sub-sequent pre-fetches
  private val mutex = Mutex()
  // the keys here are in the format $method:$url
  private var preFetched = HashMap<String, Deferred<RootNode>>()

  private fun createPreFetchKey(request: ViewRequest): String {
    return "${request.method}:${request.url}"
  }

  private suspend fun fetchView(request: ViewRequest): RootNode {
    val coreHeaders = mapOf(
      // "Content-Type" to "application/json", fixme: ktor doesn't like this header
      "platform" to nimbus.platform,
    )
    val url = nimbus.urlBuilder.build(request.url)
    val response: ServerDrivenResponse
    try {
      try {
        response = nimbus.httpClient.sendRequest(
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

      if (response.status < FIRST_BAD_STATUS) return nimbus.nodeBuilder.buildFromJsonString(response.body)
      throw ResponseError(response.status, response.body)
    } catch (e: Throwable) {
      if (request.fallback == null) throw e
      nimbus.logger.error("Failed to perform network request to $url, using the provided fallback view instead. " +
        "Cause:\n${e.message ?: "Unknown"}")
      return nimbus.nodeBuilder.buildFromJsonMap(request.fallback)
    }
  }

  override suspend fun fetch(request: ViewRequest): RootNode {
    val key = createPreFetchKey(request)
    val deferred = preFetched[key]
    preFetched = HashMap()
    if (deferred == null) return fetchView(request)
    return try {
      deferred.await()
    } catch (e: Throwable) {
      fetchView(request)
    }
  }

  /**
   * Pre-fetches a view (UI tree) from the server and stores it in memory. The next fetch will get the stored value
   * instead of performing a network request.
   *
   * Rules:
   * - A prefetch is identified by a string in the format "method:url".
   * - A call to `fetch` uses the prefetched result (if there's one) and removes all prefetched views from the pool.
   * - A prefetch for a view that has already been prefetched will do nothing.
   * - A fetch call for a view that has been prefetched, but the request has not yet finished will await the response
   * instead of making another network call.
   * - Fetch makes another request if the prefetch failed.
   *
   * @param request the data for the request to make.
   */
  override fun preFetch(request: ViewRequest) {
    CoroutineScope(Dispatchers.Default).launch {
      mutex.withLock {
        val key = createPreFetchKey(request)
        val deferred = preFetched[key]
        if (deferred == null) {
          preFetched[key] = this.async {
            try {
              return@async fetchView(request)
            } catch (e: Throwable) {
              this.cancel("Error while prefetching.\n${e.message}")
              return@async RootNode()
            }
          }
        }
      }
    }
  }
}
