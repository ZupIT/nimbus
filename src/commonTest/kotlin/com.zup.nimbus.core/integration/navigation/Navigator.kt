package com.zup.nimbus.core.integration.navigation

import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.ServerDrivenNavigator
import com.zup.nimbus.core.network.NetworkError
import com.zup.nimbus.core.network.ViewRequest
import com.zup.nimbus.core.tree.MalformedComponentError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope

@OptIn(ExperimentalCoroutinesApi::class)
class Navigator(
  private val scope: TestScope,
  private val nimbus: Nimbus,
  private val onError: (e: Error) -> Unit,
  private val onLoad: (pages: ArrayList<Page>) -> Unit,
): ServerDrivenNavigator {
  private val pages = ArrayList<Page>()

  override fun push(request: ViewRequest) {
    val view = nimbus.createView(this)
    pages.add(Page(request.url, view))
    scope.launch {
      try {
        val tree = nimbus.viewClient.fetch(request)
        view.renderer.paint(tree)
        onLoad(pages)
      } catch (e: NetworkError) {
        onError(e)
      } catch (e: MalformedComponentError) {
        onError(e)
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
