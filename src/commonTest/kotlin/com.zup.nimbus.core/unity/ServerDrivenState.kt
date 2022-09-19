package com.zup.nimbus.core.unity

import com.zup.nimbus.core.ServerDrivenState
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

