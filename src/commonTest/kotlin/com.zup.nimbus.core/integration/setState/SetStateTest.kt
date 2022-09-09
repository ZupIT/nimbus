package com.zup.nimbus.core.integration.setState

import com.zup.nimbus.core.EmptyNavigator
import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.NodeUtils
import com.zup.nimbus.core.ObservableLogger
import com.zup.nimbus.core.ServerDrivenConfig
import com.zup.nimbus.core.log.LogLevel
import com.zup.nimbus.core.ServerDrivenView
import com.zup.nimbus.core.tree.stateful.ServerDrivenNode
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SetStateTest {
  private val logger = ObservableLogger()
  private val nimbus = Nimbus(ServerDrivenConfig("", "test", logger = logger))
  private lateinit var view: ServerDrivenView

  @BeforeTest
  fun setup() {
    logger.clear()
    view = nimbus.createView({ EmptyNavigator() })
  }

  private fun assertSetStateScreenIsCorrect(
      screen: ServerDrivenNode?,
      expectedName: String = "",
      expectedAge: Int = 0,
      expectedButtonText: String = "aaa",
  ) {
    // THEN there should be 2 rows: 1 of texts and another of buttons
    assertEquals(2, screen?.children?.size)
    // AND there should be 2 text components in the first row
    val textRow = screen?.children?.get(0)
    assertEquals(2, textRow?.children?.size)
    // AND the name should be correct
    assertEquals("Name: $expectedName", textRow?.children?.get(0)?.properties?.get("text"))
    // AND the age should be correct
    assertEquals("Age: $expectedAge", textRow?.children?.get(1)?.properties?.get("text"))
    // AND there should be 3 buttons in the second row
    val buttonRow = screen?.children?.get(1)
    assertEquals(3, buttonRow?.children?.size)
    // AND the text of the 3rd button text should be correct
    assertEquals(
      "Set this next text to bbb: $expectedButtonText",
      buttonRow?.children?.get(2)?.properties?.get("text"),
    )
  }

  @Test
  fun `should set states`() {
    // WHEN the GENERAL_SET_STATE is rendered
    view.render(GENERAL_SET_STATE)
    // THEN nothing should have been logged
    assertTrue(logger.entries.isEmpty())
    // AND the screen should have the correct content
    val currentUI = view.getRendered()?.children?.first()
    assertSetStateScreenIsCorrect(currentUI)

    // WHEN we press the button to set the name
    NodeUtils.pressButton(currentUI, "setName")
    // THEN nothing should have been logged
    assertTrue(logger.entries.isEmpty())
    // AND the screen should have the correct content
    assertSetStateScreenIsCorrect(currentUI, "John")

    // WHEN we press the button to set the age
    NodeUtils.pressButton(currentUI, "setAge")
    // THEN nothing should have been logged
    assertTrue(logger.entries.isEmpty())
    // AND the screen should have the correct content
    assertSetStateScreenIsCorrect(currentUI, "John", 30)

    // WHEN we press the button to set the button text
    NodeUtils.pressButton(currentUI, "setButtonText")
    // THEN nothing should have been logged
    assertTrue(logger.entries.isEmpty())
    // AND the screen should have the correct content
    assertSetStateScreenIsCorrect(currentUI, "John", 30, "bbb")
  }

  @Test
  fun `should not set state`() {
    // WHEN the UNREACHABLE_STATE screen is rendered
    view.render(UNREACHABLE_STATE)
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    val currentUI = view.getRendered()?.children?.first()
    // WHEN the onInit lifecycle is run
    NodeUtils.triggerEvent(currentUI, "onInit")
    // THEN an error should've been logged
    assertEquals(1, logger.entries.size)
    assertEquals(LogLevel.Error, logger.entries.first().level)
    // AND the text content should not have been changed
    assertEquals("", currentUI?.children?.get(0)?.properties?.get("text"))
  }

  @Test
  fun `should change the state type`() {
    // WHEN the MANY_TYPES screen is rendered
    view.render(MANY_TYPES)
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    val currentUI = view.getRendered()?.children?.first()
    // AND text should be the String "string"
    assertEquals("string", currentUI?.children?.get(0)?.properties?.get("text"))

    // WHEN we click the button to set the state value to Int
    NodeUtils.pressButton(currentUI, "setInt")
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be the Int 10
    assertEquals(10, currentUI?.children?.get(0)?.properties?.get("text"))

    // WHEN we click the button to set the state value to Double
    NodeUtils.pressButton(currentUI, "setDouble")
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be the Int 5.64
    assertEquals(5.64, currentUI?.children?.get(0)?.properties?.get("text"))

    // WHEN we click the button to set the state value to Array
    NodeUtils.pressButton(currentUI, "setArray")
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be the Array [0, 1, 2]
    assertEquals(listOf(0, 1, 2), currentUI?.children?.get(0)?.properties?.get("text"))

    // WHEN we click the button to set the state value to Map
    NodeUtils.pressButton(currentUI, "setMap")
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be the Map { a: 0, b: 1 }
    assertEquals(mapOf("a" to 0, "b" to 1), currentUI?.children?.get(0)?.properties?.get("text"))

    // WHEN we click the button to set the state value to Boolean
    NodeUtils.pressButton(currentUI, "setBoolean")
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be the Boolean true
    assertEquals(true, currentUI?.children?.get(0)?.properties?.get("text"))

    // WHEN we click the button to set the state value to null
    NodeUtils.pressButton(currentUI, "setNull")
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be null
    assertEquals(null, currentUI?.children?.get(0)?.properties?.get("text"))

    // WHEN we click the button to set the state value to String
    NodeUtils.pressButton(currentUI, "setString")
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be the String "string"
    assertEquals("string", currentUI?.children?.get(0)?.properties?.get("text"))
  }

  @Test
  fun `should set deep state path`() {
    // WHEN the DEEP_STATE screen is rendered
    view.render(DEEP_STATE)
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    val currentUI = view.getRendered()?.children?.first()
    // AND text should be { a: { b: { c: { d: { e: 0, f: 1 }, g: 2 } } } }
    assertEquals(
      mapOf("a" to mapOf("b" to mapOf("c" to mapOf("d" to mapOf("e" to 0, "f" to 1), "g" to 2)))),
      currentUI?.children?.get(0)?.properties?.get("text"),
    )

    // WHEN we press the button to set set test.a.b.h.i to 3
    NodeUtils.pressButton(currentUI, "abhiTo3")
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be { a: { b: { c: { d: { e: 0, f: 1 }, g: 2 }, h: { i: 3 } } } }
    assertEquals(
      mapOf("a" to mapOf("b" to mapOf("c" to mapOf("d" to mapOf("e" to 0, "f" to 1), "g" to 2), "h" to mapOf("i" to 3)))),
      currentUI?.children?.get(0)?.properties?.get("text"),
    )

    // WHEN we press the button to set test.a.b.c.d.e to 4
    NodeUtils.pressButton(currentUI, "abcdeTo4")
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be { a: { b: { c: { d: { e: 4, f: 1 }, g: 2 }, h: { i: 3 } } } }
    assertEquals(
      mapOf("a" to mapOf("b" to mapOf("c" to mapOf("d" to mapOf("e" to 4, "f" to 1), "g" to 2), "h" to mapOf("i" to 3)))),
      currentUI?.children?.get(0)?.properties?.get("text"),
    )

    // WHEN we press the button to set test.a.b to 5
    NodeUtils.pressButton(currentUI, "abTo5")
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be { a: { b: { 5 } }
    assertEquals(mapOf("a" to mapOf("b" to 5)), currentUI?.children?.get(0)?.properties?.get("text"))
  }

  @Test
  fun `should set global state`() {
    // WHEN the GLOBAL_STATE screen is rendered
    view.render(GLOBAL_STATE)
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    val currentUI = view.getRendered()?.children?.first()
    // AND text should be Hey !
    assertEquals("Hey !", currentUI?.children?.get(0)?.properties?.get("text"))

    // WHEN we press the button to set the username
    NodeUtils.pressButton(currentUI, "setUserName")
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be Hey John!
    assertEquals("Hey John!", currentUI?.children?.get(0)?.properties?.get("text"))
  }
}
