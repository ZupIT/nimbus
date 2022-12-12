/*
 * Copyright 2023 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.zup.nimbus.core

import br.com.zup.nimbus.core.network.NetworkError
import br.com.zup.nimbus.core.network.ViewRequest
import br.com.zup.nimbus.core.tree.dynamic.builder.MalformedComponentError
import br.com.zup.nimbus.core.tree.dynamic.node.RootNode
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

