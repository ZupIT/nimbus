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

package br.com.zup.nimbus.core.unity.utils

import br.com.zup.nimbus.core.utils.transformJsonArrayToList
import br.com.zup.nimbus.core.utils.transformJsonObjectToMap
import br.com.zup.nimbus.core.utils.transformJsonPrimitiveToPrimitive
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.double
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class JsonTest {
  @Test
  fun `should transform a JsonObject to an object map`() {
    val jsonObject = Json.decodeFromString<JsonObject>("""{ "query": "Pizza", "location": 94043 }""")
    val result = transformJsonObjectToMap(jsonObject)
    assertContains(result, "query")
    assertContains(result, "location")
    assertEquals("Pizza", result["query"])
    assertEquals(94043, result["location"])
  }

  @Test
  fun `should transform a JsonArray to a List`() {
    val jsonArray = Json.decodeFromString<JsonArray>("""[{ "food": "Pizza" }, { "food": "Hamburger" }]""")
    val result = transformJsonArrayToList(jsonArray)
    assertNotNull(result)
    assertEquals(2, result.size)
    assertContains(result[0] as Map<String, Any?>, "food")
    assertContains(result[1] as Map<String, Any?>, "food")
    assertEquals((result[0] as Map<String, Any?>)["food"], "Pizza")
    assertEquals((result[1] as Map<String, Any?>)["food"], "Hamburger")
  }

  @Test
  fun `should transform primitives to their correct types`() {
    var primitive = JsonPrimitive(2)
    var value = transformJsonPrimitiveToPrimitive(primitive)
    assertEquals(2, value)

    primitive = JsonPrimitive(false)
    value = transformJsonPrimitiveToPrimitive(primitive)
    assertEquals(false, value)

    primitive = JsonPrimitive("Text")
    value = transformJsonPrimitiveToPrimitive(primitive)
    assertEquals("Text", value)

    primitive = JsonPrimitive(13.14)
    value = transformJsonPrimitiveToPrimitive(primitive)
    assertEquals(13.14, value)

    primitive = JsonPrimitive(234.24836132786)
    value = transformJsonPrimitiveToPrimitive(primitive)
    assertEquals(234.24836132786, value)
  }

  @Test
  fun `should cast a JsonElement to the correct type`() {
    var element = Json.parseToJsonElement("""{ "query": "Pizza", "location": 94043 }""")
    assertContains(element.jsonObject, "query")
    assertContains(element.jsonObject, "location")
    assertEquals("Pizza", (element.jsonObject["query"]?.jsonPrimitive?.content))
    assertEquals(94043, (element.jsonObject["location"]?.jsonPrimitive?.int))

    element = Json.parseToJsonElement("""[{ "food": "Pizza" }, { "food": "Hamburger" }]""")
    assertNotNull(element)
    assertEquals(2, element.jsonArray.size)
    assertContains(element.jsonArray[0].jsonObject, "food")
    assertContains(element.jsonArray[1].jsonObject, "food")
    assertEquals("Pizza", element.jsonArray[0].jsonObject["food"]?.jsonPrimitive?.content)
    assertEquals("Hamburger", element.jsonArray[1].jsonObject["food"]?.jsonPrimitive?.content)

    element = Json.parseToJsonElement("""2""")
    assertEquals(2, element.jsonPrimitive.int)

    element = Json.parseToJsonElement("""false""")
    assertEquals(false, element.jsonPrimitive.boolean)

    element = Json.parseToJsonElement("""Text""")
    assertEquals("Text", element.jsonPrimitive.content)

    element = Json.parseToJsonElement("""13.14""")
    assertEquals(13.14, element.jsonPrimitive.double)

    element = Json.parseToJsonElement("""234.24836132786""")
    assertEquals(234.24836132786, element.jsonPrimitive.double)
  }
}
