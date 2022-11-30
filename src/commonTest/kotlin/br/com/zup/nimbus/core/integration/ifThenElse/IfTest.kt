package br.com.zup.nimbus.core.integration.ifThenElse

import br.com.zup.nimbus.core.EmptyHttpClient
import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.NodeUtils
import br.com.zup.nimbus.core.ObservableLogger
import br.com.zup.nimbus.core.ServerDrivenConfig
import br.com.zup.nimbus.core.tree.ServerDrivenNode
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class IfTest {
  private fun assertThenContent(content: List<ServerDrivenNode>?, hasButton: Boolean = false) {
    // THEN the if should be replaced by 2 (or 3 if it has a button) components
    assertEquals(if (hasButton) 3 else 2, content?.size)
    // AND the text of the first component should be "Good morning"
    assertEquals("Good morning!", content?.get(0)?.properties?.get("text"))
    // AND the image of the second component should be "sun"
    assertEquals("sun", content?.get(1)?.properties?.get("id"))
  }

  private fun assertElseContent(content: List<ServerDrivenNode>?, hasButton: Boolean = false) {
    // THEN the if should be replaced by 2 (or 3 if it has a button) components
    assertEquals(if (hasButton) 3 else 2, content?.size)
    // AND the text of the first component should be "Good evening"
    assertEquals("Good evening!", content?.get(0)?.properties?.get("text"))
    // AND the image of the second component should be "moon"
    assertEquals("moon", content?.get(1)?.properties?.get("id"))
  }

  @Test
  fun `should render the content of Then when condition is true and no Else exists`() {
    // WHEN a screen with if (condition = true) and then is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test", httpClient = EmptyHttpClient))
    val content = nimbus.nodeBuilder.buildFromJsonString(createIfThenElseScreen(true))
    content.initialize(nimbus)
    val ifResult = content.children?.first()?.children
    assertThenContent(ifResult)
  }

  @Test
  fun `should render nothing when condition is false and no Else exists`() {
    // WHEN a screen with if (condition = false) and then is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test", httpClient = EmptyHttpClient))
    val content = nimbus.nodeBuilder.buildFromJsonString(createIfThenElseScreen(false))
    content.initialize(nimbus)
    val ifResult = content.children?.first()?.children
    // THEN the if component should be removed
    assertEquals(0, ifResult?.size)
  }

  @Test
  fun `should render the content of Then when condition is true and Else exists`() {
    // WHEN a screen with if (condition = true), then and else is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test", httpClient = EmptyHttpClient))
    val content = nimbus.nodeBuilder.buildFromJsonString(createIfThenElseScreen(true, includeElse = true))
    content.initialize(nimbus)
    val ifResult = content.children?.first()?.children
    assertThenContent(ifResult)
  }

  @Test
  fun `should render the content of Else when condition is false and Else exists`() {
    // WHEN a screen with if (condition = false), then and else is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test", httpClient = EmptyHttpClient))
    val content = nimbus.nodeBuilder.buildFromJsonString(createIfThenElseScreen(false, includeElse = true))
    content.initialize(nimbus)
    val ifResult = content.children?.first()?.children
    assertElseContent(ifResult)
  }

  @Test
  fun `should render nothing when a component different than Then or Else is passed to If`() {
    // WHEN a screen with if and an invalid component is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test", httpClient = EmptyHttpClient))
    val content = nimbus.nodeBuilder.buildFromJsonString(
      createIfThenElseScreen(false, includeInvalid = true)
    )
    content.initialize(nimbus)
    val ifResult = content.children?.first()?.children
    // THEN the if component should be removed
    assertEquals(0, ifResult?.size)
  }

  @Test
  fun `should render the content of Else when If has no Then and condition is false`() {
    // WHEN a screen with if and else (but no then) is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test", httpClient = EmptyHttpClient))
    val content = nimbus.nodeBuilder.buildFromJsonString(createIfThenElseScreen(
      false,
      includeThen = false,
      includeElse = true,
    ))
    content.initialize(nimbus)
    val ifResult = content.children?.first()?.children
    assertElseContent(ifResult)
  }

  @Test
  fun `should toggle then-else content`() {
    // WHEN a screen with if (condition = true) and then is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test", httpClient = EmptyHttpClient))
    val content = nimbus.nodeBuilder.buildFromJsonString(
      createIfThenElseScreen(true, includeElse = true, includeButton = true)
    )
    content.initialize(nimbus)
    val column = content.children?.first()
    NodeUtils.pressButton(column, "toggle")
    assertElseContent(column?.children, true)
    NodeUtils.pressButton(column, "toggle")
    assertThenContent(column?.children, true)
  }

  @Test
  fun `should create if-then-else behavior when if is the root node and declare its state`() {
    // WHEN a screen with if as the root component is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test", httpClient = EmptyHttpClient))
    val content = nimbus.nodeBuilder.buildFromJsonString(simpleRootIf)
    content.initialize(nimbus)
    // THEN the content of then should be rendered
    assertEquals(1, content?.children?.size)
    assertEquals("toggle-true", content?.children?.get(0)?.id)
    // WHEN the button to toggle the state is pressed
    NodeUtils.pressButton(content, "toggle-true")
    // THEN the content of else should be rendered
    assertEquals(1, content?.children?.size)
    assertEquals("toggle-false", content?.children?.get(0)?.id)
  }

  @Test
  fun `should log error when condition is not provided`() {
    val logger = ObservableLogger()
    // WHEN a screen with an invalid if is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test", httpClient = EmptyHttpClient, logger = logger))
    val content = nimbus.nodeBuilder.buildFromJsonString(INVALID_IF)
    content.initialize(nimbus)
    assertEquals(1, logger.entries.size)
    val error = logger.entries.first().message
    assertContains(error, "Unexpected value type at \"condition\". Expected \"Boolean\", found \"null\"")
  }
}
