package br.com.zup.nimbus.core

import br.com.zup.nimbus.core.network.DefaultHttpClient
import br.com.zup.nimbus.core.network.HttpClient
import br.com.zup.nimbus.core.network.ServerDrivenRequest
import br.com.zup.nimbus.core.network.ServerDrivenResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class HttpClientEntry (
  val request: ServerDrivenRequest,
  val response: Deferred<ServerDrivenResponse>
)

class ObservableHttpClient(
  private val clientToObserve: HttpClient = DefaultHttpClient(),
): HttpClient {
  var entries = ArrayList<HttpClientEntry>()
  var delayMs: Long = 0
  var delayMsPerUrl = HashMap<String, Long>()
  private val mutex = Mutex()

  fun clear(shouldClearDelays: Boolean = true) {
    entries = ArrayList()
    if (shouldClearDelays) {
      delayMs = 0
      delayMsPerUrl = HashMap()
    }
  }

  fun hasFetchedUrl(url: String): Boolean {
    return entries.find { it.request.url == url } != null
  }

  suspend fun awaitAllCurrentRequestsToFinish() {
    val deferredResponses = entries.map { it.response }
    deferredResponses.awaitAll()
  }

  override suspend fun sendRequest(request: ServerDrivenRequest): ServerDrivenResponse {
    val deferred: Deferred<ServerDrivenResponse>
    mutex.withLock {
      deferred = CoroutineScope(Dispatchers.Default).async {
        if (delayMsPerUrl[request.url] != null) delay(delayMsPerUrl[request.url]!!)
        else if (delayMs > 0) delay(delayMs)
        clientToObserve.sendRequest(request)
      }
      entries.add(HttpClientEntry(request, deferred))
    }
    return deferred.await()
  }
}
