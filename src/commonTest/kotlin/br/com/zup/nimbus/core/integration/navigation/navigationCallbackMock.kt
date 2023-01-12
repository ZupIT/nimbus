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

package br.com.zup.nimbus.core.integration.navigation

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel

private const val LIST_SCREEN = """{
  "_:component": "forEach",
  "state": {
    "notes": [
      {
        "id": 1,
        "title": "My first note",
        "description": "Description of my first note"
      },
      {
        "id": 2,
        "title": "My second note",
        "description": "Description of my second note"
      }
    ]
  },
  "properties": {
    "items": "@{notes}",
    "key": "id"
  },
  "children": [
    {
      "_:component": "layout:touchable",
      "id": "edit",
      "properties": {
        "onPress": [
          {
            "_:action": "push",
            "properties": {
              "url": "/edit",
              "state": {
                "note": "@{item}"
              },
              "events": {
                "onNoteSaved": [
                  {
                    "_:action": "test:saveNote",
                    "properties": {
                      "note": "@{onNoteSaved}"
                    }
                  },
                  {
                    "_:action": "setState",
                    "properties": {
                      "path": "item.title",
                      "value": "@{onNoteSaved.title}"
                    }
                  },
                  {
                    "_:action": "setState",
                    "properties": {
                      "path": "item.description",
                      "value": "@{onNoteSaved.description}"
                    }
                  }
                ]
              }
            }
          }
        ]
      },
      "children": [
        {
          "_:component": "layout:text",
          "id": "text",
          "properties": {
            "text": "@{item.title}: @{item.description}"
          }
        }
      ]
    }
  ]
}"""

private const val EDIT_SCREEN = """{
  "_:component": "layout:column",
  "id": "form",
  "state": {
    "title": "@{note.title}",
    "description": "@{note.description}"
  },
  "children": [
    {
      "_:component": "test:textInput",
      "id": "title",
      "properties": {
        "label": "Title",
        "value": "@{title}",
        "onChange": [{
          "_:action": "setState",
          "properties": {
            "path": "title",
            "value": "@{onChange}"
          }
        }]
      }
    },
    {
      "_:component": "test:textInput",
      "id": "description",
      "properties": {
        "label": "Description",
        "value": "@{description}",
        "onChange": [{
          "_:action": "setState",
          "properties": {
            "path": "description",
            "value": "@{onChange}"
          }
        }]
      }
    },
    {
      "_:component": "test:button",
      "id": "save",
      "properties": {
        "label": "Save",
        "onPress": [
          {
            "_:action": "triggerViewEvent",
            "properties": {
              "event": "onNoteSaved",
              "value": "@{object('title', title, 'description', description, 'id', note.id)}"
            }
          },
          { "_:action": "pop" }
        ]
      }
    },
    {
      "_:component": "test:button",
      "id": "cancel",
      "properties": {
        "label": "Cancel",
        "onPress": [{ "_:action": "pop" }]
      }
    }
  ]
}"""

val callbackServerMock = MockEngine { request ->
  return@MockEngine when(request.url.toString()) {
    "$BASE_URL/list" -> respond(
      content = ByteReadChannel(LIST_SCREEN),
      status = HttpStatusCode.OK,
      headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
    "$BASE_URL/edit" -> respond(
      content = ByteReadChannel(EDIT_SCREEN),
      status = HttpStatusCode.OK,
      headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
    else -> respond(
      content = ByteReadChannel(""),
      status = HttpStatusCode.NotFound,
    )
  }
}
