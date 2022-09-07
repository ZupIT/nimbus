package com.zup.nimbus.core.integration.ifThenElse

import com.zup.nimbus.core.EmptyNavigator
import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.ServerDrivenConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IfTest {
  @Test
  fun `should render the content of Then when condition is true and no Else exists`() {
    // WHEN a screen with if (condition = true) and then is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test"))
    val page = nimbus.createView(getNavigator = { EmptyNavigator() })
    page.render(createIfThenElseScreen(true))
    val ifResult = page.getRendered()?.children?.first()?.children
    // THEN the if should be replaced by 2 components
    assertEquals(2, ifResult?.size)
    // AND the text of the first component should be "Good morning"
    assertEquals("Good morning!", ifResult?.get(0)?.properties?.get("text"))
    // AND the image of the second component should be "sun"
    assertEquals("sun", ifResult?.get(1)?.properties?.get("id"))
  }

  /*@Test
  fun `should render nothing when condition is false and no Else exists`() {
    // WHEN a screen with if (condition = false) and then is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test"))
    val node = nimbus.createNodeFromJson(createIfThenElseScreen(false))
    val page = nimbus.createView({ EmptyNavigator() })
    var hasRendered = false
    page.renderer.paint(node)
    page.onChange {
      val ifResult = it.children
      // THEN the if component should be removed
      assertEquals(0, ifResult?.size)
      hasRendered = true
    }
    assertTrue(hasRendered)
  }

  @Test
  fun `should render the content of Then when condition is true and Else exists`() {
    // WHEN a screen with if (condition = true), then and else is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test"))
    val node = nimbus.createNodeFromJson(createIfThenElseScreen(true, includeElse = true))
    val page = nimbus.createView({ EmptyNavigator() })
    var hasRendered = false
    page.renderer.paint(node)
    page.onChange {
      val ifResult = it.children
      // THEN the if should be replaced by 2 components
      assertEquals(2, ifResult?.size)
      // AND the text of the first component should be "Good morning"
      assertEquals("Good morning!", ifResult?.get(0)?.properties?.get("text"))
      // AND the image of the second component should be "sun"
      assertEquals("sun", ifResult?.get(1)?.properties?.get("id"))
      hasRendered = true
    }
    assertTrue(hasRendered)
  }

  @Test
  fun `should render the content of Else when condition is false and Else exists`() {
    // WHEN a screen with if (condition = false), then and else is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test"))
    val node = nimbus.createNodeFromJson(createIfThenElseScreen(false, includeElse = true))
    val page = nimbus.createView({ EmptyNavigator() })
    var hasRendered = false
    page.renderer.paint(node)
    page.onChange {
      val ifResult = it.children
      // THEN the if should be replaced by 2 components
      assertEquals(2, ifResult?.size)
      // AND the text of the first component should be "Good evening"
      assertEquals("Good evening!", ifResult?.get(0)?.properties?.get("text"))
      // AND the image of the second component should be "moon"
      assertEquals("moon", ifResult?.get(1)?.properties?.get("id"))
      hasRendered = true
    }
    assertTrue(hasRendered)
  }

  @Test
  fun `should fail when a component different than Then or Else is passed to If`() {
    // WHEN a screen with if and an invalid component is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test"))
    val node = nimbus.createNodeFromJson(createIfThenElseScreen(false, includeInvalid = true))
    val page = nimbus.createView({ EmptyNavigator() })
    var error: Throwable? = null
    try {
      page.renderer.paint(node)
    } catch (e: Throwable) {
      error = e
    }
    // Then it should fail
    assertTrue(error is UnexpectedComponentError)
  }

  @Test
  fun `should fail when If has no Then`() {
    // WHEN a screen with if and else (but no then) is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test"))
    val node = nimbus.createNodeFromJson(createIfThenElseScreen(
      false,
      includeThen = false,
      includeElse = true,
    ))
    val page = nimbus.createView({ EmptyNavigator() })
    var error: Throwable? = null
    try {
      page.renderer.paint(node)
    } catch (e: Throwable) {
      error = e
    }
    // Then it should fail
    assertTrue(error is MissingComponentError)
  }*/
}
