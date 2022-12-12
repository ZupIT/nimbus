/*
 * Copyright 2023 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    "greetings": {
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
        "firstName": "John"
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

