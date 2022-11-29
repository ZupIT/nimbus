package com.zup.nimbus.core

import com.zup.nimbus.core.network.NetworkError
import com.zup.nimbus.core.network.ViewRequest
import com.zup.nimbus.core.tree.dynamic.builder.MalformedComponentError
import com.zup.nimbus.core.tree.dynamic.node.RootNode
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestScope

@OptIn(ExperimentalCoroutinesApi::class)
class ObservableNavigator(
  private val testScope: TestScope,
  private val nimbus: Nimbus,
): ServerDrivenNavigator {
  var pages = ArrayList<RootNode>()
  private var deferredPush: CompletableDeferred<RootNode>? = null

  suspend fun awaitPushCompletion(): RootNode {
    deferredPush = CompletableDeferred()
    return deferredPush!!.await()
  }

  fun clear() {
    pages = ArrayList()
  }

  override fun push(request: ViewRequest) {
    val states = request.params?.map { ServerDrivenState(it.key, it.value) }
    val view = ServerDrivenView(nimbus, states = states, description = request.url) { this }
    testScope.launch {
      try {
        val tree = nimbus.viewClient.fetch(request)
        tree.initialize(view)
        pages.add(tree)
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

