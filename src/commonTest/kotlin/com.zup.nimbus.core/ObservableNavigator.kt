package com.zup.nimbus.core

import com.zup.nimbus.core.network.NetworkError
import com.zup.nimbus.core.network.ViewRequest
import com.zup.nimbus.core.tree.MalformedComponentError
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestScope

@OptIn(ExperimentalCoroutinesApi::class)
class ObservableNavigator(
  private val scope: TestScope,
  private val nimbus: Nimbus,
): ServerDrivenNavigator {
  private var pages = ArrayList<Page>()
  private var deferredPush: CompletableDeferred<Page>? = null

  suspend fun awaitPushCompletion(): Page {
    deferredPush = CompletableDeferred()
    return deferredPush!!.await()
  }

  fun clear() {
    pages = ArrayList()
  }

  override fun push(request: ViewRequest) {
    val view = nimbus.createView({ this })
    pages.add(Page(request.url, view))
    scope.launch {
      try {
        val tree = nimbus.viewClient.fetch(request)
        view.renderer.paint(tree)
        deferredPush?.complete(pages.last())
      } catch (e: NetworkError) {
        deferredPush?.cancel(e.message, e)
      } catch (e: MalformedComponentError) {
        deferredPush?.cancel(e.message, e)
      }
    }
  }

  override fun pop() {
    pages.removeLast()
  }

  override fun popTo(url: String) {
    // no need create an integration test for this (similar to pop)
  }

  override fun present(request: ViewRequest) {
    // no need create an integration test for this (similar to push)
  }

  override fun dismiss() {
    // no need create an integration test for this (similar to pop)
  }
}
