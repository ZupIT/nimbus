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

package br.com.zup.nimbus.core.integration.operations

import br.com.zup.nimbus.core.EmptyHttpClient
import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.NodeUtils
import br.com.zup.nimbus.core.ObservableLogger
import br.com.zup.nimbus.core.ServerDrivenConfig
import br.com.zup.nimbus.core.integration.sendRequest.BASE_URL
import br.com.zup.nimbus.core.tree.findNodeById
import br.com.zup.nimbus.core.ui.UILibrary
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class OperationsTest {
  private val logger = ObservableLogger()
  private var operationACallCount = 0
  private var operationBCallCount = 0
  private val ui = UILibrary("test")
    .addOperation("myOperationA") {
      operationACallCount++
      it[0]
    }
    .addOperation("myOperationB") {
      operationBCallCount++
      it[0]
    }

  private val nimbus = Nimbus(
    ServerDrivenConfig(
      baseUrl = BASE_URL,
      platform = "test",
      httpClient = EmptyHttpClient,
      logger = logger,
      ui = listOf(ui)
    )
  )

  @BeforeTest
  fun resetCallCount() {
    operationACallCount = 0
    operationBCallCount = 0
  }

  @Test
  fun `should add 1 to the count everytime the button is pressed`() {
    val tree = nimbus.nodeBuilder.buildFromJsonString(FIRST_PAGE)
    tree.initialize(nimbus)
    val content = NodeUtils.getContent(tree)
    var count = content.states?.first()?.value as Number
    assertEquals(1, count)

    NodeUtils.pressButton(content, "addToCount")

    count = content.states?.first()?.value as Number
    assertEquals(2, count)

    NodeUtils.pressButton(content, "addToCount")

    count = content.states?.first()?.value as Number
    assertEquals(3, count)
  }

  @Test
  fun `should run condition`() {
    val tree = nimbus.nodeBuilder.buildFromJsonString(CONDITION_TEST)
    tree.initialize(nimbus)
    val content = NodeUtils.getContent(tree)
    val result = content.findNodeById("result")?.properties?.get("text")
    assertEquals(null, result)
  }

  @Test
  fun `should rerun operation for text content if state changes`() {
    val tree = nimbus.nodeBuilder.buildFromJsonString(STATE_CHANGE)
    tree.initialize(nimbus)
    resetCallCount()
    NodeUtils.pressButton(tree, "count")
    assertEquals(1, operationACallCount)
    assertEquals(1, operationBCallCount)
    assertEquals(1, tree.findNodeById("countText")?.properties?.get("text"))
  }

  @Test
  fun `should not run operations inside action when the action is initialized`() {
    val tree = nimbus.nodeBuilder.buildFromJsonString(ACTION_OPERATION)
    tree.initialize(nimbus)
    assertEquals(0, operationACallCount)
    assertEquals(0, operationBCallCount)
  }

  @Test
  fun `should not run operations inside action just because one of its arguments got updated`() {
    val tree = nimbus.nodeBuilder.buildFromJsonString(ACTION_OPERATION)
    tree.initialize(nimbus)
    resetCallCount()
    NodeUtils.pressButton(tree, "count")
    assertEquals(0, operationACallCount)
    assertEquals(0, operationBCallCount)
  }

  @Test
  fun `should run operations when action is called`() {
    val tree = nimbus.nodeBuilder.buildFromJsonString(ACTION_OPERATION)
    tree.initialize(nimbus)
    resetCallCount()
    NodeUtils.pressButton(tree, "log")
    assertEquals(1, operationACallCount)
    assertEquals(1, operationBCallCount)
  }
}
