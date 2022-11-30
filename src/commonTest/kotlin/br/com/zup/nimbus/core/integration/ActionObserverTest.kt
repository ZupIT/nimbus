package br.com.zup.nimbus.core.integration

import br.com.zup.nimbus.core.ActionTriggeredEvent
import br.com.zup.nimbus.core.EmptyHttpClient
import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.NodeUtils
import br.com.zup.nimbus.core.ObservableLogger
import br.com.zup.nimbus.core.ServerDrivenConfig
import br.com.zup.nimbus.core.tree.ServerDrivenAction
import br.com.zup.nimbus.core.tree.ServerDrivenNode
import br.com.zup.nimbus.core.tree.findNodeById
import br.com.zup.nimbus.core.ui.UILibrary
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AnalyticsRecord(
  val platform: String,
  val action: ServerDrivenAction,
  val node: ServerDrivenNode,
  val event: String,
  val timestamp: Long,
)

/**
 * A simple analytics service that creates an analytics record if the action has `analytics = true` in its metadata.
 */

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
      node = event.scope.node,
      event = event.scope.name,
      timestamp = 98844454548L, // in a real implementation, get the current unix time
    ))
  }
}

private const val SCREEN = """{
  "_:component": "layout:container",
  "children": [
    {
      "_:component": "layout:layoutHandler",
      "id": "init",
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
      "id": "btn-without-analytics",
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
      "id": "btn-with-analytics",
      "properties": {
        "text": "Push with analytics",
        "onPress": [{
          "_:action": "log",
          "properties": {
            "message": "Pressed"
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
  private val nimbus = Nimbus(
    ServerDrivenConfig(
      baseUrl = "",
      platform = "",
      logger = logger,
      ui = listOf(UILibrary().addActionObserver { analytics.createRecord(it) }),
      httpClient = EmptyHttpClient,
    )
  )

  @BeforeTest
  fun clear() {
    logger.clear()
    analytics.clear()
  }

  @Test
  fun `should create an  analytics record for log`() {
    val screen = nimbus.nodeBuilder.buildFromJsonString(SCREEN)
    screen.initialize(nimbus)
    val layoutHandler = screen.findNodeById("init")
    NodeUtils.triggerEvent(layoutHandler, "onInit")
    assertEquals(1, logger.entries.size)
    assertEquals(1, analytics.entries.size)
    val record = analytics.entries.first()
    assertEquals("Test", record.platform)
    assertEquals("log", record.action.name)
    assertEquals(layoutHandler, record.node)
    assertEquals("onInit", record.event)
    assertEquals(98844454548L, record.timestamp)
  }

  @Test
  fun `should not create an analytics record for log`() {
    val screen = nimbus.nodeBuilder.buildFromJsonString(SCREEN)
    screen.initialize(nimbus)
    NodeUtils.pressButton(screen, "btn-without-analytics")
    assertEquals(1, logger.entries.size)
    assertEquals(0, analytics.entries.size)
  }

  @Test
  fun `should create an analytics record for push`() {
    val screen = nimbus.nodeBuilder.buildFromJsonString(SCREEN)
    screen.initialize(nimbus)
    NodeUtils.pressButton(screen, "btn-with-analytics")
    assertEquals(1, analytics.entries.size)
    val record = analytics.entries.first()
    assertEquals("Test", record.platform)
    assertEquals("log", record.action.name)
    assertEquals(screen.findNodeById("btn-with-analytics"), record.node)
    assertEquals("onPress", record.event)
    assertEquals(98844454548L, record.timestamp)
  }
}
