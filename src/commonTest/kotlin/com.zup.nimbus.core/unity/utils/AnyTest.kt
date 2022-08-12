package com.zup.nimbus.core.unity.utils

import com.zup.nimbus.core.utils.UnexpectedDataTypeError
import com.zup.nimbus.core.utils.transformJsonObjectToMap
import com.zup.nimbus.core.utils.valueOfPath
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlin.test.*

private const val JSON_TEST = """{
  "id": "001",
  "age": 23,
  "power": 9000.53,
  "isAlive": true,
  "relationships": [
    {
      "name": { "real": { "first": "Tien" } },
      "types": [{ "type": "Brother", "duration": 10 }]
    },
    {
      "name": { "real": { "first": "Shallan" } },
      "types": [
        { "type": "Friend", "duration": 3 },
        { "type": "RomanticInterest", "duration": 1 }
      ]
    }
  ],
  "name": {
    "real": {
      "first": "Kaladin",
      "last": "Stormblessed",
      "title": "Captain of the King's Guard"
    },
    "alias": "Kal"
  }
}"""

private val jsonObject = Json.decodeFromString<JsonObject>(JSON_TEST)
private val testData = transformJsonObjectToMap(jsonObject)

class AnyTest {
  // Tests for the function "valueOf"

  // Tests without path
  @Test
  fun `should get a string value`() {
    val result: String = valueOfPath("test")
    assertEquals("test", result)
  }

  @Test
  fun `should get a string value or null`() {
    val result: String? = valueOfPath(null)
    assertEquals(null, result)
  }

  @Test
  fun `should correctly infer null coalescence`() {
    val result: String = valueOfPath(null) ?: "test"
    assertEquals("test", result)
  }

  @Test
  fun `should throw when getting a null as string`() {
    var error: Throwable? = null
    try {
      valueOfPath<String>(null)
    } catch (e: Throwable) {
      error = e
    }
    assertTrue(error is UnexpectedDataTypeError)
    assertEquals("String", error.expectedType.simpleName)
    assertEquals(null, error.valueFound)
  }

  @Test
  fun `should throw when getting an int as string`() {
    var error: Throwable? = null
    try {
      valueOfPath<String>(5)
    } catch (e: Throwable) {
      error = e
    }
    assertTrue(error is UnexpectedDataTypeError)
    assertEquals("String", error.expectedType.simpleName)
    assertEquals(5, error.valueFound)
  }

  @Test
  fun `should get an int value`() {
    val result: Int = valueOfPath(10)
    assertEquals(10, result)
  }

  @Test
  fun `should get a double value`() {
    val result: Double = valueOfPath(10.22)
    assertEquals(10.22, result)
  }

  @Test
  fun `should get a boolean value`() {
    val result: Boolean = valueOfPath(false)
    assertEquals(false, result)
  }

  @Test
  fun `should get a list value`() {
    val result: List<Any> = valueOfPath(listOf("1", 2, false, 4.2))
    assertEquals(listOf("1", 2, false, 4.2), result)
  }

  // Tests with path
  @Test
  fun `should get a value at path`() {
    val id: String = valueOfPath(testData, "id")
    val age: Int = valueOfPath(testData, "age")
    val power: Double = valueOfPath(testData, "power")
    val isAlive: Boolean = valueOfPath(testData, "isAlive")
    val relationships: List<String> = valueOfPath(testData, "relationships")
    val name: Map<String, Any> = valueOfPath(testData, "name")
    assertEquals("001", id)
    assertEquals(23, age)
    assertEquals(9000.53, power)
    assertEquals(true, isAlive)
    assertEquals(2, relationships.size)
    assertEquals(2, name.keys.size)
  }

  @Test
  fun `should get a value at deep path`() {
    val firstName: String = valueOfPath(testData, "name.real.first")
    val lastName: String = valueOfPath(testData, "name.real.last")
    val title: String = valueOfPath(testData, "name.real.title")
    val alias: String = valueOfPath(testData, "name.alias")
    assertEquals("Kaladin", firstName)
    assertEquals("Stormblessed", lastName)
    assertEquals("Captain of the King's Guard", title)
    assertEquals("Kal", alias)
  }

  @Test
  fun `should get null if path doesn't exist`() {
    val firstName: String? = valueOfPath(testData, "name.first")
    val lastName: String? = valueOfPath(testData, "name.real.other.last")
    assertEquals(null, firstName)
    assertEquals(null, lastName)
  }

  @Test
  fun `should get an array value at index`() {
    val array = listOf("1", 2, null)
    val first: String = valueOfPath(array, "[0]")
    val second: Int = valueOfPath(array, "[1]")
    val third: Int? = valueOfPath(array, "[2]")
    assertEquals("1", first)
    assertEquals(2, second)
    assertEquals(null, third)
  }

  @Test
  fun `should get null when index is out of bounds`() {
    val array = listOf("1", 2, null)
    val outOfBounds: Int? = valueOfPath(array, "[10]")
    assertEquals(null, outOfBounds)
  }

  @Test
  fun `should throw when index is out of bounds`() {
    var error: Throwable? = null
    val array = listOf("1", 2, null)
    try {
      valueOfPath<Int>(array, "[10]")
    } catch (e: Throwable) {
      error = e
    }
    assertTrue(error is UnexpectedDataTypeError)
    assertEquals("Int", error.expectedType.simpleName)
    assertEquals(null, error.valueFound)
  }

  @Test
  fun `should get null when index of map is accessed`() {
    val result: String? = valueOfPath(testData, "name[0]")
    assertEquals(null, result)
  }

  @Test
  fun `should throw when index of map is accessed`() {
    var error: Throwable? = null
    try {
      valueOfPath<String>(testData, "name[0]")
    } catch (e: Throwable) {
      error = e
    }
    assertTrue(error is UnexpectedDataTypeError)
    assertEquals("String", error.expectedType.simpleName)
    assertEquals(null, error.valueFound)
  }

  @Test
  fun `should throw if path doesn't exist`() {
    var error: Throwable? = null
    try {
      valueOfPath<String>(testData, "name.real.other.last")
    } catch (e: Throwable) {
      error = e
    }
    assertTrue(error is UnexpectedDataTypeError)
    assertEquals("String", error.expectedType.simpleName)
    assertEquals(null, error.valueFound)
    assertEquals("name.real.other.last", error.path)
  }

  @Test
  fun `should get path with special characters`() {
    val data = mapOf(
      "_:component" to "test",
      "onPress" to listOf(
        mapOf(":**/action/:__" to "anotherTest")
      )
    )
    val component: String = valueOfPath(data, "_:component")
    val action: String = valueOfPath(data, "onPress[0].:**/action/:__")
    assertEquals("test", component)
    assertEquals("anotherTest", action)
  }

  @Test
  fun `should get values from array at path`() {
    val tienName: String = valueOfPath(testData, "relationships[0].name.real.first")
    val tienRelationshipType: String = valueOfPath(testData, "relationships[0].types[0].type")
    val tienRelationshipDuration: Int = valueOfPath(testData, "relationships[0].types[0].duration")
    val shallanName: String = valueOfPath(testData, "relationships[1].name.real.first")
    val shallanRelationship1Type: String = valueOfPath(testData, "relationships[1].types[0].type")
    val shallanRelationship1Duration: Int = valueOfPath(testData, "relationships[1].types[0].duration")
    val shallanRelationship2Type: String = valueOfPath(testData, "relationships[1].types[1].type")
    val shallanRelationship2Duration: Int = valueOfPath(testData, "relationships[1].types[1].duration")
    assertEquals("Tien", tienName)
    assertEquals("Brother", tienRelationshipType)
    assertEquals(10, tienRelationshipDuration)
    assertEquals("Shallan", shallanName)
    assertEquals("Friend", shallanRelationship1Type)
    assertEquals(3, shallanRelationship1Duration)
    assertEquals("RomanticInterest", shallanRelationship2Type)
    assertEquals(1, shallanRelationship2Duration)
  }
}
