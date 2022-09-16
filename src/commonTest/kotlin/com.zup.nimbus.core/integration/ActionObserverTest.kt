package com.zup.nimbus.core.integration

import com.zup.nimbus.core.tree.ServerDrivenAction
import com.zup.nimbus.core.tree.node.ServerDrivenNode

class AnalyticsRecord(
  val platform: String,
  val action: ServerDrivenAction,
  val node: ServerDrivenNode,
  val event: String,
  val screen: String,
  val timestamp: Long,
)

/**
 * A simple analytics service that creates an analytics record if the action has `analytics = true` in its metadata.
 */
/*
class MyAnalyticsService {
  var entries = ArrayList<AnalyticsRecord>()

  fun clear() {
    entries = ArrayList()
  }

  fun createRecord(event: ActionTriggeredEvent) {
    if (event.action.metadata?.get("analytics") != true) return
    entries.add(AnalyticsRecord(
      platform = "Test",
      action = event.action,
      node = event.origin,
      event = event.name,
      screen = event.view.description ?: "unknown",
      timestamp = 98844454548L, // in a real implementation, get the current unix time
    ))
  }
}

private const val SCREEN = """{
  "_:component": "layout:container",
  "children": [
    {
      "_:component": "layout:layoutHandler",
      "properties": {
        "onInit": [{
          "_:action": "log",
          "properties": {
            "message": "view started",
            "level": "Warning"
          },
          "metadata": {
            "analytics": true
          }
        }]
      }
    },
    {
      "_:component": "material:button",
      "properties": {
        "text": "Log without analytics",
        "onPress": [{
          "_:action": "log",
          "properties": {
            "message": "Hello"
          }
        }]
      }
    },
    {
      "_:component": "material:button",
      "properties": {
        "text": "Push with analytics",
        "onPress": [{
          "_:action": "push",
          "properties": {
            "url": "/screen2"
          },
          "metadata": {
            "analytics": true
          }
        }]
      }
    }
  ]
}"""

class ActionObserverTest {
  private val logger = ObservableLogger()
  private val analytics = MyAnalyticsService()
  private val nimbus = Nimbus(ServerDrivenConfig(
    baseUrl = "",
    platform = "",
    logger = logger,
    actionObservers = listOf({ analytics.createRecord(it) }),
  ))

  @BeforeTest
  fun clear() {
    logger.clear()
    analytics.clear()
  }

  @Test
  fun `should create an  analytics record for log`() {
    val view = nimbus.createView({ EmptyNavigator() }, "json")
    val screen = RenderNode.fromJsonString(SCREEN, nimbus.idManager)
    var hasRendered = false
    view.renderer.paint(screen)
    view.onChange {
      val layoutHandler = it.children!![0]
      NodeUtils.triggerEvent(layoutHandler, "onInit")
      assertEquals(1, logger.entries.size)
      assertEquals(1, analytics.entries.size)
      val record = analytics.entries.first()
      assertEquals("Test", record.platform)
      assertEquals("log", record.action.action)
      assertEquals(layoutHandler, record.node)
      assertEquals("onInit", record.event)
      assertEquals("json", record.screen)
      assertEquals(98844454548L, record.timestamp)
      hasRendered = true
    }
    assertTrue(hasRendered)
  }

  @Test
  fun `should not create an analytics record for log`() {
    val view = nimbus.createView({ EmptyNavigator() }, "json")
    val screen = RenderNode.fromJsonString(SCREEN, nimbus.idManager)
    var hasRendered = false
    view.renderer.paint(screen)
    view.onChange {
      val button = it.children!![1]
      NodeUtils.triggerEvent(button, "onPress")
      assertEquals(1, logger.entries.size)
      assertEquals(0, analytics.entries.size)
      hasRendered = true
    }
    assertTrue(hasRendered)
  }

  @Test
  fun `should create an analytics record for push`() {
    val view = nimbus.createView({ EmptyNavigator() }, "json")
    val screen = RenderNode.fromJsonString(SCREEN, nimbus.idManager)
    var hasRendered = false
    view.renderer.paint(screen)
    view.onChange {
      val button = it.children!![2]
      NodeUtils.triggerEvent(button, "onPress")
      assertEquals(1, analytics.entries.size)
      val record = analytics.entries.first()
      assertEquals("Test", record.platform)
      assertEquals("push", record.action.action)
      assertEquals(button, record.node)
      assertEquals("onPress", record.event)
      assertEquals("json", record.screen)
      assertEquals(98844454548L, record.timestamp)
      hasRendered = true
    }
    assertTrue(hasRendered)
  }
}
*/
