package com.zup.nimbus.core.unity.deserialization

import com.zup.nimbus.core.deserialization.AnyServerDrivenData
import com.zup.nimbus.core.tree.ServerDrivenEvent
import com.zup.nimbus.core.unity.SimpleEvent
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class AnyServerDrivenData {
  private val stringValue = "test"
  private val intValue = 20
  private val doubleValue = 15.2
  private val floatValue = 20.63F
  private val longValue = 30L
  private val booleanValue = true
  private val mapValue = mapOf("a" to 1, "b" to 2)
  private val listValue = listOf(1, 2)
  private val eventValue = SimpleEvent()

  private lateinit var stringData: AnyServerDrivenData
  private lateinit var intData: AnyServerDrivenData
  private lateinit var doubleData: AnyServerDrivenData
  private lateinit var floatData: AnyServerDrivenData
  private lateinit var longData: AnyServerDrivenData
  private lateinit var booleanData: AnyServerDrivenData
  private lateinit var nullData: AnyServerDrivenData
  private lateinit var mapData: AnyServerDrivenData
  private lateinit var listData: AnyServerDrivenData
  private lateinit var eventData: AnyServerDrivenData

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
  }

  private fun deserializeAndCheckResult(
    expectedString: Any? = "",
    expectedInt: Any? = 0,
    expectedDouble: Any? = 0.0,
    expectedFloat: Any? = 0F,
    expectedLong: Any? = 0L,
    expectedBoolean: Any? = false,
    expectedNull: Any? = AnyServerDrivenData.any,
    expectedMap: Any? = emptyMap<String, AnyServerDrivenData>(),
    expectedList: Any? = emptyList<AnyServerDrivenData>(),
    expectedEventName: Any? = "Unknown",
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
    val deserializedEventName = (deserialize(eventData) as ServerDrivenEvent).name
    assertEquals(expectedString, deserializedString)
    assertEquals(expectedInt, deserializedInt)
    assertEquals(expectedDouble, deserializedDouble)
    assertEquals(expectedFloat, deserializedFloat)
    assertEquals(expectedLong, deserializedLong)
    assertEquals(expectedBoolean, deserializedBoolean)
    assertEquals(expectedNull, deserializedNull)
    assertEquals(expectedMap, deserializedMap)
    assertEquals(expectedList, deserializedList)
    assertEquals(expectedEventName, deserializedEventName)
  }

  private fun checkErrors(expectedErrors: Map<AnyServerDrivenData, String> = emptyMap()) {
    val allData = listOf(stringData, intData, doubleData, floatData, longData, booleanData, nullData, mapData,
      listData, eventData)
    allData.forEach { data ->
      if (expectedErrors.containsKey(data)) {
        assertTrue(data.hasError())
        assertContains(data.errorsAsString(), expectedErrors[data]!!)
      } else {
        assertFalse(data.hasError())
      }
    }
  }

  private fun error(expected: String, actual: String, property: String = "") =
    "Expected $expected for property \"$property\", but found $actual."

  @Test
  fun `should deserialize using asAnyOrNull`() {
    deserializeAndCheckResult(
      expectedString = stringValue,
      expectedInt = intValue,
      expectedDouble = doubleValue,
      expectedFloat = floatValue,
      expectedLong = longValue,
      expectedBoolean = booleanValue,
      expectedNull = null,
      expectedMap = mapValue,
      expectedList = listValue,
      expectedEventName = eventValue.name,
    ) { it.asAnyOrNull() }
    checkErrors()
  }

  @Test
  fun `should deserialize using asAny`() {
    deserializeAndCheckResult(
      expectedString = stringValue,
      expectedInt = intValue,
      expectedDouble = doubleValue,
      expectedFloat = floatValue,
      expectedLong = longValue,
      expectedBoolean = booleanValue,
      expectedMap = mapValue,
      expectedList = listValue,
      expectedEventName = eventValue.name,
    ) { it.asAny() }
    checkErrors(mapOf(
      nullData to error("anything", "null")
    ))
  }

  @Test
  fun `should deserialize using asStringOrNull`() {
    deserializeAndCheckResult(
      expectedString = stringValue,
      expectedInt = "$intValue",
      expectedDouble = "$doubleValue",
      expectedFloat = "$floatValue",
      expectedLong = "$longValue",
      expectedBoolean = "$booleanValue",
      expectedNull = null,
      expectedMap = "$mapValue",
      expectedList = "$listValue",
      expectedEventName = eventValue.name,
    ) { it.asAnyOrNull() }
    checkErrors()
  }

  @Test
  fun `should deserialize using asString`() {
    deserializeAndCheckResult(
      expectedString = stringValue,
      expectedInt = intValue,
      expectedDouble = doubleValue,
      expectedFloat = floatValue,
      expectedLong = longValue,
      expectedBoolean = booleanValue,
      expectedMap = mapValue,
      expectedList = listValue,
      expectedEventName = eventValue.name,
    ) { it.asAny() }
    checkErrors(mapOf(
      nullData to error("anything", "null")
    ))
  }

  @Test
  fun `should deserialize and coerce Int types`() {

  }

  @Test
  fun `should yield error when deserializing something that is not a number as Int`() {

  }

  @Test
  fun `should deserialize and coerce Long types`() {

  }

  @Test
  fun `should yield error when deserializing something that is not a number as Long`() {

  }

  @Test
  fun `should deserialize and coerce Float types`() {

  }

  @Test
  fun `should yield error when deserializing something that is not a number as Float`() {

  }

  @Test
  fun `should deserialize and coerce Double types`() {

  }

  @Test
  fun `should yield error when deserializing something that is not a number as Double`() {

  }

  @Test
  fun `should deserialize Boolean`() {

  }

  @Test
  fun `should yield error when deserializing something that is not a Boolean as Boolean`() {

  }

  @Test
  fun `should deserialize List`() {

  }

  @Test
  fun `should yield error when deserializing something that is not a List as List`() {

  }

  @Test
  fun `should deserialize Map`() {

  }

  @Test
  fun `should yield error when deserializing something that is not a Map as Map`() {

  }

  @Test
  fun `should deserialize ServerDrivenEvent`() {

  }

  @Test
  fun `should yield error when deserializing something that is not a ServerDrivenEvent as a ServerDrivenEvent`() {

  }

  @Test
  fun `should correctly check types`() {

  }

  @Test
  fun `should correctly verify existence of key in map`() {

  }

  @Test
  fun `should correctly verify existence of key in map with value other than null`() {

  }

  @Test
  fun `should correctly verify existence of at least one key in map`() {

  }

  @Test
  fun `should correctly verify existence of element in list`() {

  }

  @Test
  fun `should correctly verify existence of element other than null at list index`() {

  }

  @Test
  fun `should output list size`() {

  }

  @Test
  fun `should output map size`() {

  }

  @Test
  fun `should create child AnyServerDrivenData according to key - map`() {

  }

  @Test
  fun `should create child AnyServerDrivenData according to position - list`() {

  }
}
