package com.zup.nimbus.core.integration

import com.zup.nimbus.core.EmptyNavigator
import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.ServerDrivenConfig
import com.zup.nimbus.core.integration.forEach.GENERAL_FOR_EACH
import kotlin.test.Test
import kotlin.test.assertTrue

private const val SCREEN = """{
  "_:component": "layout:container",
  "children": [
    {
      "_:component": "material:text",
      "properties": {
        "text": "Screen 1"
      }
    },
    {
      "_:component": "material:button",
      "properties": {
        "text": "Next",
        "onPress": [{
          "_:action": "push",
          "properties": {
            "url": "/screen2.json"
          }
        }]
      }
    }
  ]
}"""

class BorrachaTest {
  @Test
  fun test() {
    val nimbus = Nimbus(ServerDrivenConfig("", "test"))
    val node = nimbus.createNodeFromJson(SCREEN)
    val page = nimbus.createView({ EmptyNavigator() })
    var hasRendered = false
    page.renderer.paint(node)
    page.onChange {
      hasRendered = true
    }
    assertTrue(hasRendered)
  }
}
