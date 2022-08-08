package com.zup.nimbus.core.unity.tree

import com.zup.nimbus.core.tree.ObservableState
import kotlin.test.Test
import kotlin.test.assertEquals

class ObservableStateTest {
  @Test
  fun `should create a primitive typed state with a determined id`() {
    val stateId = "testState"
    val stateValue = "Test state value"
    val state = ObservableState(stateId, stateValue)

    assertEquals(stateId, state.id)
    assertEquals(stateValue, state.value)
  }

  @Test
  fun `should update the value of a primitive typed state`() {
    val stateId = "testState"
    val stateValue = "Test state value"
    val stateUpdatedValue = "This is the updated test state value"
    val state = ObservableState(stateId, stateValue)

    assertEquals(stateValue, state.value)
    state.set(stateUpdatedValue, "")
    assertEquals(stateUpdatedValue, state.value)
  }

  @Test
  fun `should update the value of a primitive typed state and notify the listeners with the new state value`() {
    val stateId = "testState"
    val stateValue = "Test state value"
    val stateUpdatedValue = "This is the updated test state value"
    val state = ObservableState(stateId, stateValue)
    state.onChange { assertEquals(stateUpdatedValue, it) }

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
    val state = ObservableState(stateId, stateValue)

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
    val state = ObservableState(stateId, stateValue)
    assertEquals(stateValue, state.value)
    assertEquals("bar", (state.value as Map<*, *>)["b"])

    state.set("foo bar", "b")
    assertEquals("foo bar", (state.value as Map<*, *>)["b"])
  }

  @Test
  fun `should update an object's attribute value from the state and notify the listeners with the new state value`() {
    val stateId = "testState"
    val stateValue = mapOf ("a" to "foo", "b" to "bar")
    val state = ObservableState(stateId, stateValue)
    state.onChange {
      assertEquals((it as Map<*, *>)["a"], "foo")
      assertEquals(it["b"], "foo bar")
    }
    assertEquals(stateValue, state.value)
    assertEquals("bar", (state.value as Map<*, *>)["b"])

    state.set("foo bar", "b")
    assertEquals("foo bar", (state.value as Map<*, *>)["b"])
  }

  @Test
  fun `should create an list state with a determined id`() {
    val stateId = "testState"
    val stateValue = listOf ("a", "b", "c")
    val state = ObservableState(stateId, stateValue)

    assertEquals(stateId, state.id)
    assertEquals(stateValue, state.value)
  }

  @Test
  fun `should update an list's value from the state`() {
    val stateId = "testState"
    val stateValue = listOf ("a", "b", "c")
    val stateUpdatedValue = listOf ("a", "foo bar", "c")
    val state = ObservableState(stateId, stateValue)
    assertEquals(stateValue, state.value)
    assertEquals("b", (state.value as List<*>)[1])

    state.set(stateUpdatedValue, "")
    assertEquals("a", (state.value as List<*>)[0])
    assertEquals("foo bar", (state.value as List<*>)[1])
    assertEquals("c", (state.value as List<*>)[2])
  }

  @Test
  fun `should update an list's value from the state and notify the listeners with the new state value`() {
    val stateId = "testState"
    val stateValue = listOf ("a", "b", "c")
    val stateUpdatedValue = listOf ("a", "foo bar", "c")
    val state = ObservableState(stateId, stateValue)
    state.onChange {
      assertEquals("a", (it as List<*>)[0])
      assertEquals("foo bar", it[1])
      assertEquals("c", it[2])
    }

    assertEquals(stateValue, state.value)
    assertEquals("b", (state.value as List<*>)[1])

    state.set(stateUpdatedValue, "")
    assertEquals("a", (state.value as List<*>)[0])
    assertEquals("foo bar", (state.value as List<*>)[1])
    assertEquals("c", (state.value as List<*>)[2])
  }
}
