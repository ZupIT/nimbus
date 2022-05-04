package com.zup.nimbus.core.unit.utils

import com.zup.nimbus.core.unit.UnexpectedDataTypeError
import com.zup.nimbus.core.unit.transformJsonObjectToMap
import com.zup.nimbus.core.unit.valueOf
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlin.test.*

private const val JSON_TEST = """{
  "id": "001",
  "age": 23,
  "power": 9000.53,
  "isAlive": true,
  "relationships": ["Tien", "Tarah", "Laral", "Sil", "Moash"],
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
  fun shouldGetStringValue() {
    val result: String = valueOf("test")
    assertEquals("test", result)
  }

  @Test
  fun shouldGetStringValueOrNull() {
    val result: String? = valueOf(null)
    assertEquals(null, result)
  }

  @Test
  fun shouldCorrectlyInferNullCoalescence() {
    val result: String = valueOf(null) ?: "test"
    assertEquals("test", result)
  }

  @Test
  fun shouldThrowWhenGettingNullAsString() {
    var error: Throwable? = null
    try {
      valueOf<String>(null)
    } catch (e: Throwable) {
      error = e
    }
    assertTrue(error is UnexpectedDataTypeError)
    assertEquals("String", error.expectedType.simpleName)
    assertEquals(null, error.valueFound)
  }

  @Test
  fun shouldThrowWhenGettingIntAsString() {
    var error: Throwable? = null
    try {
      valueOf<String>(5)
    } catch (e: Throwable) {
      error = e
    }
    assertTrue(error is UnexpectedDataTypeError)
    assertEquals("String", error.expectedType.simpleName)
    assertEquals(5, error.valueFound)
  }

  @Test
  fun shouldGetIntValue() {
    val result: Int = valueOf(10)
    assertEquals(10, result)
  }

  @Test
  fun shouldGetDoubleValue() {
    val result: Double = valueOf(10.22)
    assertEquals(10.22, result)
  }

  @Test
  fun shouldGetBooleanValue() {
    val result: Boolean = valueOf(false)
    assertEquals(false, result)
  }

  @Test
  fun shouldGeListValue() {
    val result: List<Any> = valueOf(listOf("1", 2, false, 4.2))
    assertEquals(listOf("1", 2, false, 4.2), result)
  }

  // Tests with path
  @Test
  fun shouldGetValueAtPath() {
    val id: String = valueOf(testData, "id")
    val age: Int = valueOf(testData, "age")
    val power: Double = valueOf(testData, "power")
    val isAlive: Boolean = valueOf(testData, "isAlive")
    val relationships: List<String> = valueOf(testData, "relationships")
    val name: Map<String, Any> = valueOf(testData, "name")
    assertEquals("001", id)
    assertEquals(23, age)
    assertEquals(9000.53, power)
    assertEquals(true, isAlive)
    assertEquals(listOf("Tien", "Tarah", "Laral", "Sil", "Moash"), relationships)
    assertEquals(2, name.keys.size)
  }

  @Test
  fun shouldGetValueAtDeepPath() {
    val firstName: String = valueOf(testData, "name.real.first")
    val lastName: String = valueOf(testData, "name.real.last")
    val title: String = valueOf(testData, "name.real.title")
    val alias: String = valueOf(testData, "name.alias")
    assertEquals("Kaladin", firstName)
    assertEquals("Stormblessed", lastName)
    assertEquals("Captain of the King's Guard", title)
    assertEquals("Kal", alias)
  }

  @Test
  fun shouldGetNullIfPathDoesntExist() {
    val firstName: String? = valueOf(testData, "name.first")
    val lastName: String? = valueOf(testData, "name.real.other.last")
    assertEquals(null, firstName)
    assertEquals(null, lastName)
  }

  @Test
  fun shouldThrowIfPathDoesntExist() {
    var error: Throwable? = null
    try {
      valueOf<String>(testData, "name.real.other.last")
    } catch (e: Throwable) {
      error = e
    }
    assertTrue(error is UnexpectedDataTypeError)
    assertEquals("String", error.expectedType.simpleName)
    assertEquals(null, error.valueFound)
    assertEquals("name.real.other.last", error.path)
  }
}
