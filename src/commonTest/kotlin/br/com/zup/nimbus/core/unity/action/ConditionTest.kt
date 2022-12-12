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

package br.com.zup.nimbus.core.unity.action

import br.com.zup.nimbus.core.ActionTriggeredEvent
import br.com.zup.nimbus.core.EmptyHttpClient
import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.ObservableLogger
import br.com.zup.nimbus.core.ServerDrivenConfig
import br.com.zup.nimbus.core.log.LogLevel
import br.com.zup.nimbus.core.scope.Scope
import br.com.zup.nimbus.core.tree.ServerDrivenEvent
import br.com.zup.nimbus.core.ui.action.condition
import br.com.zup.nimbus.core.ui.action.error.ActionDeserializationError
import br.com.zup.nimbus.core.unity.SimpleAction
import br.com.zup.nimbus.core.unity.SimpleEvent
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class ConditionTest {
  private fun createActionTriggeredEvent(
    conditionValue: Boolean? = null,
    onTrue: ServerDrivenEvent? = null,
    onFalse: ServerDrivenEvent? = null,
    parent: Scope? = null
  ): ActionTriggeredEvent {
    val action = SimpleAction("condition", { condition(it) }, mapOf(
      "condition" to conditionValue,
      "onTrue" to onTrue,
      "onFalse" to onFalse,
    ))
    return ActionTriggeredEvent(action = action, scope = SimpleEvent(parent = parent), dependencies = mutableSetOf())
  }

  @Test
  fun `should run onTrue if condition is true`() {
    val onTrue = SimpleEvent()
    val event = createActionTriggeredEvent(conditionValue = true, onTrue = onTrue)
    condition(event)
    assertEquals(1, onTrue.calls.size)
  }

  @Test
  fun `should run onFalse if condition is false`() {
    val onFalse = SimpleEvent()
    val event = createActionTriggeredEvent(conditionValue = false, onFalse = onFalse)
    condition(event)
    assertEquals(1, onFalse.calls.size)
  }

  @Test
  fun `should do nothing if condition is true and onTrue is not provided`() {
    val onFalse = SimpleEvent()
    val event = createActionTriggeredEvent(conditionValue = true, onFalse = onFalse)
    condition(event)
    assertTrue(onFalse.calls.isEmpty())
  }

  @Test
  fun `should do nothing if condition is false and onFalse is not provided`() {
    val onTrue = SimpleEvent()
    val event = createActionTriggeredEvent(conditionValue = false, onTrue = onTrue)
    condition(event)
    assertTrue(onTrue.calls.isEmpty())
  }

  @Test
  fun `should fail if condition is not provided`() {
    val nimbus = Nimbus(ServerDrivenConfig(
      baseUrl = "",
      platform = "test",
      httpClient = EmptyHttpClient,
    ))
    val event = createActionTriggeredEvent(parent = nimbus)
    try {
      condition(event)
      fail("Expected to throw")
    } catch (e: ActionDeserializationError) {
      assertContains(e.message, "Expected a boolean for property \"condition\", but found null")
    }
  }
}
