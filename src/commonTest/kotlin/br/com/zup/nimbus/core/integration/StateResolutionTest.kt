package br.com.zup.nimbus.core.integration

import br.com.zup.nimbus.core.EmptyHttpClient
import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.ServerDrivenConfig
import br.com.zup.nimbus.core.tree.findNodeById
import kotlin.test.Test
import kotlin.test.assertEquals

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
      "id": "formal",
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
        "id": "mundane",
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
    val nimbus = Nimbus(ServerDrivenConfig("", "test", httpClient = EmptyHttpClient))
    val tree = nimbus.nodeBuilder.buildFromJsonString(SCREEN)
    tree.initialize(nimbus)
    assertEquals("How are you?", tree.findNodeById("formal")?.properties?.get("text"))
    assertEquals("Whazap John?", tree.findNodeById("mundane")?.properties?.get("text"))
  }
}

