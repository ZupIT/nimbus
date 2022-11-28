package com.zup.nimbus.core.unity.action

import com.zup.nimbus.core.ActionTriggeredEvent
import com.zup.nimbus.core.EmptyHttpClient
import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.ObservableLogger
import com.zup.nimbus.core.ServerDrivenConfig
import com.zup.nimbus.core.log.LogLevel
import com.zup.nimbus.core.scope.Scope
import com.zup.nimbus.core.tree.ServerDrivenEvent
import com.zup.nimbus.core.ui.action.condition
import com.zup.nimbus.core.ui.action.error.ActionDeserializationError
import com.zup.nimbus.core.unity.SimpleAction
import com.zup.nimbus.core.unity.SimpleEvent
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
