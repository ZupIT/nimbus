package com.zup.nimbus.core.unity.deserialization

import com.zup.nimbus.core.deserialization.AnyServerDrivenData
import com.zup.nimbus.core.tree.ServerDrivenEvent
import com.zup.nimbus.core.unity.SimpleEvent
import kotlin.reflect.KClass
import kotlin.test.BeforeTest
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


open class AnyServerDrivenDataTest {
  protected enum class Animal { Cat, Dog, Cow }

  protected val stringValue = "200.5"
  protected val intValue = 20
  protected val doubleValue = 15.2
  protected val floatValue = -20.63F
  protected val longValue = 30L
  protected val booleanValue = true
  protected val mapValue = mapOf("a" to 1, "b" to 2, "c" to null)
  protected val listValue = listOf(1, 2, null, 3)
  protected val eventValue = SimpleEvent()
  protected val enumValue = "dOg"

  protected lateinit var stringData: AnyServerDrivenData
  protected lateinit var intData: AnyServerDrivenData
  protected lateinit var doubleData: AnyServerDrivenData
  protected lateinit var floatData: AnyServerDrivenData
  protected lateinit var longData: AnyServerDrivenData
  protected lateinit var booleanData: AnyServerDrivenData
  protected lateinit var nullData: AnyServerDrivenData
  protected lateinit var mapData: AnyServerDrivenData
  protected lateinit var listData: AnyServerDrivenData
  protected lateinit var eventData: AnyServerDrivenData
  protected lateinit var enumData: AnyServerDrivenData
  private lateinit var allData: Map<String, AnyServerDrivenData>

  @BeforeTest
  fun setup() {
    stringData = AnyServerDrivenData(stringValue)
    intData = AnyServerDrivenData(intValue)
    doubleData = AnyServerDrivenData(doubleValue)
    floatData = AnyServerDrivenData(floatValue)
    longData = AnyServerDrivenData(longValue)
    booleanData = AnyServerDrivenData(booleanValue)
    nullData = AnyServerDrivenData(null)
    mapData = AnyServerDrivenData(mapValue)
    listData = AnyServerDrivenData(listValue)
    eventData = AnyServerDrivenData(eventValue)
    enumData = AnyServerDrivenData(enumValue)
    allData = mapOf(
      "stringData" to stringData,
      "intData" to intData,
      "doubleData" to doubleData,
      "floatData" to floatData,
      "longData" to longData,
      "booleanData" to booleanData,
      "nullData" to nullData,
      "mapData" to mapData,
      "listData" to listData,
      "eventData" to eventData,
      "enumData" to enumData,
    )
  }

  protected fun deserializeAndCheckResult(
    expectedString: Any?,
    expectedInt: Any?,
    expectedDouble: Any?,
    expectedFloat: Any?,
    expectedLong: Any?,
    expectedBoolean: Any?,
    expectedNull: Any?,
    expectedMap: Any?,
    expectedList: Any?,
    expectedEvent: Any?,
    expectedEnum: Any?,
    deserialize: (AnyServerDrivenData) -> Any?,
  ) {
    val deserializedString = deserialize(stringData)
    val deserializedInt = deserialize(intData)
    val deserializedDouble = deserialize(doubleData)
    val deserializedFloat = deserialize(floatData)
    val deserializedLong = deserialize(longData)
    val deserializedBoolean = deserialize(booleanData)
    val deserializedNull = deserialize(nullData)
    val deserializedMap = deserialize(mapData)
    val deserializedList = deserialize(listData)
    val deserializedEvent = deserialize(eventData)
    val deserializedEnum = deserialize(enumData)
    assertEquals(expectedString, deserializedString)
    assertEquals(expectedInt, deserializedInt)
    assertEquals(expectedDouble, deserializedDouble)
    assertEquals(expectedFloat, deserializedFloat)
    assertEquals(expectedLong, deserializedLong)
    assertEquals(expectedBoolean, deserializedBoolean)
    assertEquals(expectedNull, deserializedNull)
    assertEquals(expectedMap, deserializedMap)
    assertEquals(expectedList, deserializedList)
    assertEquals(expectedEnum, deserializedEnum)
    if (expectedEvent is ServerDrivenEvent) {
      assertTrue(deserializedEvent is ServerDrivenEvent)
      assertEquals(expectedEvent.name, deserializedEvent.name, "deserialized value is not an event")
    } else {
      assertEquals(expectedEvent, deserializedEvent)
    }
  }

  protected fun checkErrors(expectedErrors: Map<AnyServerDrivenData, String> = emptyMap()) {
    allData.forEach { entry ->
      val data = entry.value
      val name = entry.key
      if (expectedErrors.containsKey(data)) {
        assertTrue(data.hasError(), "expected $name to contain errors, but it didn't")
        assertContains(data.errorsAsString(), expectedErrors[data]!!, true)
      } else {
        assertFalse(data.hasError(), "expected $name to not contain errors, but it did")
      }
    }
  }

  protected fun checkType(
    typeName: String,
    expectedMatches: List<AnyServerDrivenData>,
    check: (AnyServerDrivenData) -> Boolean,
  ) {
    allData.forEach { entry ->
      val data = entry.value
      val name = entry.key
      val result = check(data)
      if (expectedMatches.contains(data)) {
        assertTrue(result, "expected $name to be $typeName, but it wasn't")
      } else {
        assertFalse(result, "expected $name to not be $typeName, but it was")
      }
    }
  }

  protected fun checkType(
    typeName: String,
    expectedMatch: AnyServerDrivenData,
    check: (AnyServerDrivenData) -> Boolean,
  ) = checkType(typeName, listOf(expectedMatch), check)

  protected fun error(expected: String, actual: String, property: String = "") =
    "Expected $expected for property \"$property\", but found $actual."

  protected fun numberErrors() = mapOf(
    booleanData to error("a number", "boolean"),
    mapData to error("a number", "map"),
    listData to error("a number", "list"),
    eventData to error("a number", "event"),
    enumData to error("a number", "string"),
  )
}
