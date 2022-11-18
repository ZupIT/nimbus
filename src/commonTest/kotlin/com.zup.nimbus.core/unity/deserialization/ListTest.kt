package com.zup.nimbus.core.unity.deserialization

import com.zup.nimbus.core.deserialization.AnyServerDrivenData
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ListTest: AnyServerDrivenDataTest() {
  private fun errors() = mapOf(
    stringData to error("a list", "string"),
    intData to error("a list", "int"),
    longData to error("a list", "long"),
    floatData to error("a list", "float"),
    doubleData to error("a list", "double"),
    booleanData to error("a list", "boolean"),
    mapData to error("a list", "map"),
    eventData to error("a list", "event"),
  )

  private fun shouldDeserialize(
    expectedNull: Any?,
    additionalErrors: Map<AnyServerDrivenData, String> = emptyMap(),
    deserialize: (AnyServerDrivenData) -> Any?,
  ) {
    deserializeAndCheckResult(
      expectedString = AnyServerDrivenData.emptyList,
      expectedInt = AnyServerDrivenData.emptyList,
      expectedDouble = AnyServerDrivenData.emptyList,
      expectedFloat = AnyServerDrivenData.emptyList,
      expectedLong = AnyServerDrivenData.emptyList,
      expectedBoolean = AnyServerDrivenData.emptyList,
      expectedNull = expectedNull,
      expectedMap = AnyServerDrivenData.emptyList,
      expectedList = listValue.map { AnyServerDrivenData(it) },
      expectedEvent = AnyServerDrivenData.emptyList,
      deserialize = deserialize,
    )
    checkErrors(errors() + additionalErrors)
  }

  @Test
  fun `should deserialize using asListOrNull`() = shouldDeserialize(null) { it.asListOrNull() }

  @Test
  fun `should deserialize using asList`() = shouldDeserialize(
    AnyServerDrivenData.emptyList,
    mapOf(nullData to error("a list", "null")),
  ) { it.asList() }

  @Test
  fun `should correctly identify if the content of the AnyServerDrivenData is a list`() =
    checkType(typeName = "a list", expectedMatch = listData) { it.isList() }

  @Test
  fun `should correctly verify existence of non-null element at index`() {
    assertFalse(listData.hasValueForIndex(-1))
    assertTrue(listData.hasValueForIndex(0))
    assertTrue(listData.hasValueForIndex(1))
    assertFalse(listData.hasValueForIndex(2))
    assertTrue(listData.hasValueForIndex(3))
    assertFalse(listData.hasValueForIndex(4))
  }

  @Test
  fun `should correctly get the list size`() {
    assertEquals(4, listData.listSize())
    assertEquals(0, mapData.listSize())
    assertEquals(0, stringData.listSize())
    assertEquals(0, intData.listSize())
  }

  @Test
  fun `should create child AnyServerDrivenData according to index`() {
    assertEquals(listValue[0], listData.at(0).asInt())
    assertEquals(listValue[1], listData.at(1).asInt())
    assertEquals(listValue[2], listData.at(2).asIntOrNull())
    assertEquals(listValue[3], listData.at(3).asInt())
    assertEquals(null, listData.at(4).asIntOrNull())
    assertFalse(listData.hasError())
    listData.at(4).asInt()
    assertTrue(listData.hasError())
    assertContains(listData.errorsAsString(), error("a number", "null", "[4]"))
  }

  @Test
  fun `should create child AnyServerDrivenData at index even if the content is not a list`() {
    assertEquals(null, stringData.at(0).asIntOrNull())
    assertEquals(null, booleanData.at(0).asIntOrNull())
    assertEquals(null, intData.at(0).asIntOrNull())
    assertEquals(null, longData.at(0).asIntOrNull())
    assertEquals(null, floatData.at(0).asIntOrNull())
    assertEquals(null, doubleData.at(0).asIntOrNull())
    assertEquals(null, mapData.at(0).asIntOrNull())
    assertEquals(null, eventData.at(0).asIntOrNull())
    assertFalse(listData.hasError())
    stringData.at(0).asInt()
    assertTrue(stringData.hasError())
    assertContains(stringData.errorsAsString(), error("a number", "null", "[0]"))
  }
}
