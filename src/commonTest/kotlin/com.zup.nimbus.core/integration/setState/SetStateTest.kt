package com.zup.nimbus.core.integration.setState

import com.zup.nimbus.core.EmptyNavigator
import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.NodeUtils
import com.zup.nimbus.core.ObservableLogger
import com.zup.nimbus.core.ServerDrivenConfig
import com.zup.nimbus.core.render.ServerDrivenView
import com.zup.nimbus.core.tree.ServerDrivenNode
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SetStateTest {
  private val logger = ObservableLogger()
  private val nimbus = Nimbus(ServerDrivenConfig("", "test", logger = logger))
  private var currentUI: ServerDrivenNode? = null
  private var numberOfRenders = 0
  private var view: ServerDrivenView? = null

  @BeforeTest
  fun setup() {
    logger.clear()
    currentUI = null
    numberOfRenders = 0
    view = nimbus.createView({ EmptyNavigator() })
    view?.onChange {
      currentUI = it
      numberOfRenders++
    }
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
    // WHEN the SET_STATE_SCREEN is rendered
    val screen = nimbus.createNodeFromJson(SET_STATE_SCREEN)
    view!!.renderer.paint(screen)
    // THEN nothing should have been logged
    assertTrue(logger.entries.isEmpty())
    // AND the number of renders should be 1
    assertEquals(1, numberOfRenders)
    // AND the screen should have the correct content
    assertSetStateScreenIsCorrect(currentUI)

    // WHEN we press the button to set the name
    NodeUtils.pressButton(currentUI, "setName")
    // THEN nothing should have been logged
    assertTrue(logger.entries.isEmpty())
    // AND the number of renders should be 2
    assertEquals(2, numberOfRenders)
    // AND the screen should have the correct content
    assertSetStateScreenIsCorrect(currentUI, "John")

    // WHEN we press the button to set the age
    NodeUtils.pressButton(currentUI, "setAge")
    // THEN nothing should have been logged
    assertTrue(logger.entries.isEmpty())
    // AND the number of renders should be 3
    assertEquals(3, numberOfRenders)
    // AND the screen should have the correct content
    assertSetStateScreenIsCorrect(currentUI, "John", 30)

    // WHEN we press the button to set the button text
    NodeUtils.pressButton(currentUI, "setButtonText")
    // THEN nothing should have been logged
    assertTrue(logger.entries.isEmpty())
    // AND the number of renders should be 4
    assertEquals(4, numberOfRenders)
    // AND the screen should have the correct content
    assertSetStateScreenIsCorrect(currentUI, "John", 30, "bbb")
  }

  @Test
  fun `should not set state`() {

  }

  @Test
  fun `should change the state type`() {

  }

  @Test
  fun `should change deep state path`() {

  }
}
