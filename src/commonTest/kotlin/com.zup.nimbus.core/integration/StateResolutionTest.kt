package com.zup.nimbus.core.integration

import com.zup.nimbus.core.EmptyNavigator
import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.ServerDrivenConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val SCREEN = """{
  "_:component": "layout:column",
  "state": {
    "id": "greetings",
    "value": {
      "formal": "How are you",
      "mundane": "Whazap"
    }
  },
  "children": [
    {
      "_:component": "material:text",
      "properties": {
        "text": "@{greetings.formal}?"
      }
    },
    {
      "_:component": "layout:column",
      "state": {
        "id": "firstName",
        "value": "John"
      },
      "children": [{
        "_:component": "material:text",
        "properties": {
          "text": "@{greetings.mundane} @{firstName}?"
        }
      }]
    }
  ]
}"""

class StateResolutionTest {
  @Test
  fun shouldResolveStates() {
    val nimbus = Nimbus(ServerDrivenConfig("", "test"))
    val node = nimbus.createNodeFromJson(SCREEN)
    val page = nimbus.createView({ EmptyNavigator() })
    var hasRendered = false
    page.renderer.paint(node)
    page.onChange {
      assertEquals("How are you?", it.children?.get(0)?.properties?.get("text"))
      assertEquals("Whazap John?", it.children?.get(1)?.children?.get(0)?.properties?.get("text"))
      hasRendered = true
    }
    assertTrue(hasRendered)
  }
}
