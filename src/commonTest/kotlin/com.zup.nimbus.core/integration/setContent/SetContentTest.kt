package com.zup.nimbus.core.integration.setContent

import com.zup.nimbus.core.EmptyNavigator
import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.NodeUtils
import com.zup.nimbus.core.ObservableLogger
import com.zup.nimbus.core.ServerDrivenConfig
import com.zup.nimbus.core.log.LogLevel
import com.zup.nimbus.core.tree.ServerDrivenNode
import kotlin.test.Test
import kotlin.test.assertEquals

class SetContentTest {
  @Test
  fun `should set the content`() {
    // WHEN the SET_CONTENT_SCREEN is rendered
    val logger = ObservableLogger()
    val nimbus = Nimbus(ServerDrivenConfig("", "test", logger = logger))
    val node = nimbus.createNodeFromJson(SET_CONTENT_SCREEN)
    val page = nimbus.createView({ EmptyNavigator() })
    var currentUI: ServerDrivenNode? = null
    var renderCount = 0
    page.renderer.paint(node)
    page.onChange {
      currentUI = it
      renderCount++
    }
    // THEN there should be, in total, 8 nodes
    assertEquals(8, NodeUtils.flatten(currentUI).size)
    // AND the board should contain a single text: "Board"
    var board = currentUI?.children?.get(0)
    assertEquals(1, board?.children?.size)
    assertEquals("Board", board?.children?.get(0)?.properties?.get("text"))
    // WHEN the button to append a new component to the board is pressed
    NodeUtils.pressButton(currentUI, "append")
    // THEN there should be, in total, 9 nodes
    assertEquals(2, renderCount)
    assertEquals(9, NodeUtils.flatten(currentUI).size)
    // AND the board should contain two texts in the order: "Board", "A new component"
    board = currentUI?.children?.get(0)
    assertEquals(2, board?.children?.size)
    assertEquals("Board", board?.children?.get(0)?.properties?.get("text"))
    assertEquals("A new component", board?.children?.get(1)?.properties?.get("text"))
    // WHEN the button to prepend a new component to the board is pressed
    NodeUtils.pressButton(currentUI, "prepend")
    // THEN there should be, in total, 10 nodes
    assertEquals(3, renderCount)
    assertEquals(10, NodeUtils.flatten(currentUI).size)
    // AND the board should contain three texts in the order: "A new component", "Board", "A new component"
    board = currentUI?.children?.get(0)
    assertEquals(3, board?.children?.size)
    assertEquals("A new component", board?.children?.get(0)?.properties?.get("text"))
    assertEquals("Board", board?.children?.get(1)?.properties?.get("text"))
    assertEquals("A new component", board?.children?.get(2)?.properties?.get("text"))
    // AND every new node generated until now should have been assigned a unique id
    val ids = NodeUtils.flatten(currentUI).map { it.id }
    val uniqueIds = ids.distinct()
    assertEquals(ids.size, uniqueIds.size)
    // WHEN the button to replace the board's content with a new component is pressed
    NodeUtils.pressButton(currentUI, "replace")
    // THEN there should be, in total, 8 nodes
    assertEquals(4, renderCount)
    assertEquals(8, NodeUtils.flatten(currentUI).size)
    // AND the board should contain a single text: "A new component"
    assertEquals(1, board?.children?.size)
    assertEquals("A new component", board?.children?.get(0)?.properties?.get("text"))
    // WHEN the button to replace the board's content with a new component is pressed
    NodeUtils.pressButton(currentUI, "replaceItself")
    // THEN there should be, in total, 7 nodes
    assertEquals(5, renderCount)
    assertEquals(7, NodeUtils.flatten(currentUI).size)
    // THEN the board should not exist and a text written "A new component" should be in its place
    assertEquals("A new component", currentUI?.children?.get(0)?.properties?.get("text"))
    // AND no log should have been raised until now
    assertEquals(0, logger.entries.size)
    // WHEN the button to append a new component to the board is pressed again
    NodeUtils.pressButton(currentUI, "append")
    // THEN the UI should be left unchanged and an error should be logged since the board doesn't exist anymore
    assertEquals(5, renderCount)
    assertEquals(1, logger.entries.size)
    assertEquals(LogLevel.Error, logger.entries[0].level)
  }
}
