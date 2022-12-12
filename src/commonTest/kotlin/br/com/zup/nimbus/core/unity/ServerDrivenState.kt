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

package br.com.zup.nimbus.core.unity

import br.com.zup.nimbus.core.ServerDrivenState
import kotlin.test.Test
import kotlin.test.assertEquals


class ServerDrivenStateTest {
  @Test
  fun `should create a primitive typed state with a determined id`() {
    val stateId = "testState"
    val stateValue = "Test state value"
    val state = ServerDrivenState(stateId, stateValue)

    assertEquals(stateId, state.id)
    assertEquals(stateValue, state.value)
  }

  @Test
  fun `should update the value of a primitive typed state`() {
    val stateId = "testState"
    val stateValue = "Test state value"
    val stateUpdatedValue = "This is the updated test state value"
    val state = ServerDrivenState(stateId, stateValue)

    assertEquals(stateValue, state.value)
    state.set(stateUpdatedValue, "")
    assertEquals(stateUpdatedValue, state.value)
  }

  @Test
  fun `should create an object state with a determined id`() {
    val stateId = "testState"
    val stateValue = mapOf (
      "a" to "foo",
      "b" to "bar"
    )
    val state = ServerDrivenState(stateId, stateValue)

    assertEquals(stateId, state.id)
    assertEquals(stateValue, state.value)
  }

  @Test
  fun `should update an object's attribute value from the state`() {
    val stateId = "testState"
    val stateValue = mapOf (
      "a" to "foo",
      "b" to "bar"
    )
    val state = ServerDrivenState(stateId, stateValue)
    assertEquals(stateValue, state.value)
    assertEquals("bar", (state.value as Map<*, *>)["b"])

    state.set("foo bar", "b")
    assertEquals("foo bar", (state.value as Map<*, *>)["b"])
  }

  @Test
  fun `should create an list state with a determined id`() {
    val stateId = "testState"
    val stateValue = listOf ("a", "b", "c")
    val state = ServerDrivenState(stateId, stateValue)

    assertEquals(stateId, state.id)
    assertEquals(stateValue, state.value)
  }

  @Test
  fun `should update an list's value from the state`() {
    val stateId = "testState"
    val stateValue = listOf ("a", "b", "c")
    val stateUpdatedValue = listOf ("a", "foo bar", "c")
    val state = ServerDrivenState(stateId, stateValue)
    assertEquals(stateValue, state.value)
    assertEquals("b", (state.value as List<*>)[1])

    state.set(stateUpdatedValue, "")
    assertEquals("a", (state.value as List<*>)[0])
    assertEquals("foo bar", (state.value as List<*>)[1])
    assertEquals("c", (state.value as List<*>)[2])
  }
}

