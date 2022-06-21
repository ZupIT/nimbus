package com.zup.nimbus.core.unity.action

import com.zup.nimbus.core.EmptyNavigator
import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.ObservableLogger
import com.zup.nimbus.core.ServerDrivenConfig
import com.zup.nimbus.core.action.coreActions
import com.zup.nimbus.core.log.LogLevel
import com.zup.nimbus.core.render.ActionEvent
import com.zup.nimbus.core.render.ServerDrivenView
import com.zup.nimbus.core.tree.RenderAction
import com.zup.nimbus.core.tree.RenderNode
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConditionTest {
  private val conditionalAction = coreActions["condition"]!!
  private val logger = ObservableLogger()
  private val nimbus = Nimbus(ServerDrivenConfig("", "", logger = logger))

  private fun createEvent(
    conditionValue: Boolean? = null,
    onTrue: ((_: Any?) -> Unit)? = null,
    onFalse: ((_: Any?) -> Unit)? = null,
  ): ActionEvent {
    return ActionEvent(
      action = RenderAction(
        action = "condition",
        properties = mapOf("condition" to conditionValue, "onTrue" to onTrue, "onFalse" to onFalse),
        metadata = null,
        rawProperties = null,
        rawMetadata = null,
      ),
      view = ServerDrivenView(nimbus, { EmptyNavigator() }),
      name = "event",
      node = RenderNode.empty(),
    )
  }

  @BeforeTest
  fun clear() {
    logger.clear()
  }

  @Test
  fun `should run onTrue if condition is true`() {
    var called = false
    val event = createEvent(conditionValue = true, onTrue = { called = true })
    conditionalAction(event)
    assertTrue(called)
    assertTrue(logger.entries.isEmpty())
  }

  @Test
  fun `should run onFalse if condition is false`() {
    var called = false
    val event = createEvent(conditionValue = false, onFalse = { called = true })
    conditionalAction(event)
    assertTrue(called)
    assertTrue(logger.entries.isEmpty())
  }

  @Test
  fun `should do nothing if condition is true and onTrue is not provided`() {
    var called = false
    val event = createEvent(conditionValue = true, onFalse = { called = true })
    conditionalAction(event)
    assertFalse(called)
    assertTrue(logger.entries.isEmpty())
  }

  @Test
  fun `should do nothing if condition is false and onFalse is not provided`() {
    var called = false
    val event = createEvent(conditionValue = false, onTrue = { called = true })
    conditionalAction(event)
    assertFalse(called)
    assertTrue(logger.entries.isEmpty())
  }

  @Test
  fun `should fail if condition is not provided`() {
    val event = createEvent()
    conditionalAction(event)
    assertEquals(1, logger.entries.size)
    assertEquals(LogLevel.Error, logger.entries[0].level)
  }
}
