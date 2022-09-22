package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertEquals

private val condition = coreUILibrary.getOperation("condition")!!

class ConditionOperationTest {
  private val x = 0

  @Test
  fun `should return the trueValue when the condition is true`() {
    val result = condition(listOf(x < 1, "true value", "false value"))
    assertEquals("true value", result)
  }

  @Test
  fun `should return the falseValue when the condition is false`() {
    val result = condition(listOf(x > 1, "true value", "false value"))
    assertEquals("false value", result)
  }
}
