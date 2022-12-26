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

package br.com.zup.nimbus.core.integration.operations

import br.com.zup.nimbus.core.integration.sendRequest.BASE_URL
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel

const val FIRST_PAGE = """{
  "_:component": "layout:container",
  "state": {
    "count": 1
  },
  "children": [
    {
      "_:component": "material:text",
      "properties": {
        "text": "@{count]"
      }
    },
    {
      "_:component": "material:button",
      "id": "addToCount",
      "properties": {
        "text": "+1",
        "onPress": [{
          "_:action": "setState",
          "properties": {
            "path": "count",
            "value": "@{sum(count, 1)}"
          }
        }]
      }
    }
  ]
}"""

const val CONDITION_TEST = """{
  "_:component": "layout:text",
  "id": "result",
  "properties": {
    "text": "@{condition(true, null, 10)}"
  }
}"""

const val ACTION_OPERATION = """{
  "_:component": "layout:column",
  "state": {
    "counter": 0
  },
  "children": [
    {
      "_:component": "material:button",
      "id": "count",
      "properties": {
        "text": "count",
        "onPress": [
          {
            "_:action": "setState",
            "properties": {
              "path": "counter",
              "value": "@{sum(counter, 1)}"
            }
          }
        ]
      }
    },
    {
      "_:component": "material:button",
      "id": "log",
      "properties": {
        "onPress": [
          {
            "_:action": "log",
            "properties": {
              "message": "@{myOperationB(myOperationA(counter))}"
            }
          }
        ]
      }
    }
  ]
}"""

const val STATE_CHANGE = """{
  "_:component": "layout:column",
  "state": {
    "counter": 0
  },
  "children": [
    {
      "_:component": "layout:button",
      "id": "count",
      "properties": {
        "text": "count",
        "onPress": [
          {
            "_:action": "setState",
            "properties": {
              "path": "counter",
              "value": "@{sum(counter, 1)}"
            }
          }
        ]
      }
    },
    {
      "_:component": "layout:text",
      "id": "countText",
      "properties": {
        "text": "@{myOperationB(myOperationA(counter))}"
      }
    }
  ]
}"""
