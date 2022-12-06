package br.com.zup.nimbus.core.integration.setState

import br.com.zup.nimbus.core.EmptyHttpClient
import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.NodeUtils
import br.com.zup.nimbus.core.ObservableLogger
import br.com.zup.nimbus.core.ServerDrivenConfig
import br.com.zup.nimbus.core.log.LogLevel
import br.com.zup.nimbus.core.tree.ServerDrivenNode
import br.com.zup.nimbus.core.tree.findNodeById
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SetStateTest {
  private val logger = ObservableLogger()
  private val nimbus = Nimbus(ServerDrivenConfig(
    baseUrl = "",
    platform = "test",
    logger = logger,
    httpClient = EmptyHttpClient,
  ))

  @BeforeTest
  fun setup() {
    logger.clear()
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
    val tree = nimbus.nodeBuilder.buildFromJsonString(GENERAL_SET_STATE)
    tree.initialize(nimbus)
    val content = NodeUtils.getContent(tree)
    // THEN nothing should have been logged
    assertTrue(logger.entries.isEmpty())
    // AND the screen should have the correct content
    assertSetStateScreenIsCorrect(content)

    // WHEN we press the button to set the name
    NodeUtils.pressButton(content, "setName")
    // THEN nothing should have been logged
    assertTrue(logger.entries.isEmpty())
    // AND the screen should have the correct content
    assertSetStateScreenIsCorrect(content, "John")

    // WHEN we press the button to set the age
    NodeUtils.pressButton(content, "setAge")
    // THEN nothing should have been logged
    assertTrue(logger.entries.isEmpty())
    // AND the screen should have the correct content
    assertSetStateScreenIsCorrect(content, "John", 30)

    // WHEN we press the button to set the button text
    NodeUtils.pressButton(content, "setButtonText")
    // THEN nothing should have been logged
    assertTrue(logger.entries.isEmpty())
    // AND the screen should have the correct content
    assertSetStateScreenIsCorrect(content, "John", 30, "bbb")
  }

  @Test
  fun `should not set state`() {
    // WHEN the UNREACHABLE_STATE screen is rendered
    val tree = nimbus.nodeBuilder.buildFromJsonString(UNREACHABLE_STATE)
    tree.initialize(nimbus)
    val content = NodeUtils.getContent(tree)
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // WHEN the onInit lifecycle is run
    NodeUtils.triggerEvent(content, "onInit")
    // THEN an error should've been logged
    assertEquals(1, logger.entries.size)
    assertEquals(LogLevel.Error, logger.entries.first().level)
    // AND the text content should not have been changed
    assertEquals("", content.children?.get(0)?.properties?.get("text"))
  }

  @Test
  fun `should change the state type`() {
    // WHEN the MANY_TYPES screen is rendered
    val tree = nimbus.nodeBuilder.buildFromJsonString(MANY_TYPES)
    tree.initialize(nimbus)
    val content = NodeUtils.getContent(tree)
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be the String "string"
    assertEquals("string", content.children?.get(0)?.properties?.get("text"))

    // WHEN we click the button to set the state value to Int
    NodeUtils.pressButton(content, "setInt")
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be the Int 10
    assertEquals(10, content.children?.get(0)?.properties?.get("text"))

    // WHEN we click the button to set the state value to Double
    NodeUtils.pressButton(content, "setDouble")
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be the Int 5.64
    assertEquals(5.64, content.children?.get(0)?.properties?.get("text"))

    // WHEN we click the button to set the state value to Array
    NodeUtils.pressButton(content, "setArray")
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be the Array [0, 1, 2]
    assertEquals(listOf(0, 1, 2), content.children?.get(0)?.properties?.get("text"))

    // WHEN we click the button to set the state value to Map
    NodeUtils.pressButton(content, "setMap")
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be the Map { a: 0, b: 1 }
    assertEquals(mapOf("a" to 0, "b" to 1), content.children?.get(0)?.properties?.get("text"))

    // WHEN we click the button to set the state value to Boolean
    NodeUtils.pressButton(content, "setBoolean")
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be the Boolean true
    assertEquals(true, content.children?.get(0)?.properties?.get("text"))

    // WHEN we click the button to set the state value to null
    NodeUtils.pressButton(content, "setNull")
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be null
    assertEquals(null, content.children?.get(0)?.properties?.get("text"))

    // WHEN we click the button to set the state value to String
    NodeUtils.pressButton(content, "setString")
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be the String "string"
    assertEquals("string", content.children?.get(0)?.properties?.get("text"))
  }

  @Test
  fun `should set deep state path`() {
    // WHEN the DEEP_STATE screen is rendered
    val tree = nimbus.nodeBuilder.buildFromJsonString(DEEP_STATE)
    tree.initialize(nimbus)
    val content = NodeUtils.getContent(tree)
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be { a: { b: { c: { d: { e: 0, f: 1 }, g: 2 } } } }
    assertEquals(
      mapOf("a" to mapOf("b" to mapOf("c" to mapOf("d" to mapOf("e" to 0, "f" to 1), "g" to 2)))),
      content.children?.get(0)?.properties?.get("text"),
    )

    // WHEN we press the button to set set test.a.b.h.i to 3
    NodeUtils.pressButton(content, "abhiTo3")
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be { a: { b: { c: { d: { e: 0, f: 1 }, g: 2 }, h: { i: 3 } } } }
    assertEquals(
      mapOf("a" to mapOf("b" to mapOf("c" to mapOf("d" to mapOf("e" to 0, "f" to 1), "g" to 2), "h" to mapOf("i" to 3)))),
      content.children?.get(0)?.properties?.get("text"),
    )

    // WHEN we press the button to set test.a.b.c.d.e to 4
    NodeUtils.pressButton(content, "abcdeTo4")
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be { a: { b: { c: { d: { e: 4, f: 1 }, g: 2 }, h: { i: 3 } } } }
    assertEquals(
      mapOf("a" to mapOf("b" to mapOf("c" to mapOf("d" to mapOf("e" to 4, "f" to 1), "g" to 2), "h" to mapOf("i" to 3)))),
      content.children?.get(0)?.properties?.get("text"),
    )

    // WHEN we press the button to set test.a.b to 5
    NodeUtils.pressButton(content, "abTo5")
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be { a: { b: { 5 } }
    assertEquals(mapOf("a" to mapOf("b" to 5)), content.children?.get(0)?.properties?.get("text"))
  }

  @Test
  fun `should set global state`() {
    // WHEN the GLOBAL_STATE screen is rendered
    val tree = nimbus.nodeBuilder.buildFromJsonString(GLOBAL_STATE)
    tree.initialize(nimbus)
    val content = NodeUtils.getContent(tree)
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be Hey !
    assertEquals("Hey !", content.children?.get(0)?.properties?.get("text"))

    // WHEN we press the button to set the username
    NodeUtils.pressButton(content, "setUserName")
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND text should be Hey John!
    assertEquals("Hey John!", content.children?.get(0)?.properties?.get("text"))
  }

  @Test
  fun `should use multiple states in single component`() {
    // WHEN the MULTIPLE_STATES screen is rendered
    val tree = nimbus.nodeBuilder.buildFromJsonString(MULTIPLE_STATES)
    tree.initialize(nimbus)
    val content = NodeUtils.getContent(tree)
    // THEN nothing should've been logged
    assertTrue(logger.entries.isEmpty())
    // AND each button should have 0 as the its text
    assertEquals(0, content.findNodeById("incCounterA")?.properties?.get("text"))
    assertEquals(0, content.findNodeById("incCounterB")?.properties?.get("text"))
    assertEquals(0, content.findNodeById("incCounterC")?.properties?.get("text"))
    // When the button to increment counterA is pressed
    NodeUtils.pressButton(content, "incCounterA")
    // Then the button for counterA should display 1 and the others 0
    assertEquals(1, content.findNodeById("incCounterA")?.properties?.get("text"))
    assertEquals(0, content.findNodeById("incCounterB")?.properties?.get("text"))
    assertEquals(0, content.findNodeById("incCounterC")?.properties?.get("text"))
    // When teh button to increment counterA is pressed twice
    NodeUtils.pressButton(content, "incCounterB")
    NodeUtils.pressButton(content, "incCounterB")
    // Then the button for counterA should display 1, the button for counterB 2 and the button for counterC 0
    assertEquals(1, content.findNodeById("incCounterA")?.properties?.get("text"))
    assertEquals(2, content.findNodeById("incCounterB")?.properties?.get("text"))
    assertEquals(0, content.findNodeById("incCounterC")?.properties?.get("text"))
    // When teh button to increment counterA is pressed three times
    NodeUtils.pressButton(content, "incCounterC")
    NodeUtils.pressButton(content, "incCounterC")
    NodeUtils.pressButton(content, "incCounterC")
    // Then the button for counterA should display 1, the button for counterB 2 and the button for counterC 3
    assertEquals(1, content.findNodeById("incCounterA")?.properties?.get("text"))
    assertEquals(2, content.findNodeById("incCounterB")?.properties?.get("text"))
    assertEquals(3, content.findNodeById("incCounterC")?.properties?.get("text"))
  }
}
