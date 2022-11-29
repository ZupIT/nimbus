package br.com.zup.nimbus.core.unity.deserialization

import br.com.zup.nimbus.core.deserialization.AnyServerDrivenData
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MapTest: AnyServerDrivenDataTest() {
  private fun errors() = mapOf(
    stringData to error("a map", "string"),
    intData to error("a map", "int"),
    longData to error("a map", "long"),
    floatData to error("a map", "float"),
    doubleData to error("a map", "double"),
    booleanData to error("a map", "boolean"),
    listData to error("a map", "list"),
    eventData to error("a map", "event"),
    enumData to error("a map", "string"),
  )

  private fun shouldDeserialize(
    expectedNull: Any?,
    additionalErrors: Map<AnyServerDrivenData, String> = emptyMap(),
    deserialize: (AnyServerDrivenData) -> Any?,
  ) {
    deserializeAndCheckResult(
      expectedString = AnyServerDrivenData.emptyMap,
      expectedInt = AnyServerDrivenData.emptyMap,
      expectedDouble = AnyServerDrivenData.emptyMap,
      expectedFloat = AnyServerDrivenData.emptyMap,
      expectedLong = AnyServerDrivenData.emptyMap,
      expectedBoolean = AnyServerDrivenData.emptyMap,
      expectedNull = expectedNull,
      expectedMap = mapValue.mapValues { AnyServerDrivenData(it.value) },
      expectedList = AnyServerDrivenData.emptyMap,
      expectedEvent = AnyServerDrivenData.emptyMap,
      expectedEnum = AnyServerDrivenData.emptyMap,
      deserialize = deserialize,
    )
    checkErrors(errors() + additionalErrors)
  }

  @Test
  fun `should deserialize using asMapOrNull`() = shouldDeserialize(null) { it.asMapOrNull() }

  @Test
  fun `should deserialize using asMap`() = shouldDeserialize(
    AnyServerDrivenData.emptyMap,
    mapOf(nullData to error("a map", "null")),
  ) { it.asMap() }

  @Test
  fun `should correctly generate path when using asMap`() {
    val map = mapData.asMap()
    assertEquals("a", map["a"]?.path)
    assertEquals("b", map["b"]?.path)
    assertEquals("c", map["c"]?.path)
  }

  @Test
  fun `should correctly identify if the content of the AnyServerDrivenData is a map`() =
    checkType(typeName = "a map", expectedMatch = mapData) { it.isMap() }

  @Test
  fun `should correctly verify existence of key with value other than null`() {
    assertTrue(mapData.hasValueForKey("a"))
    assertTrue(mapData.hasValueForKey("b"))
    assertFalse(mapData.hasValueForKey("c"))
    assertFalse(mapData.hasValueForKey("d"))
    assertFalse(listData.hasValueForKey("1"))
  }

  @Test
  fun `should correctly verify existence of at least one key`() {
    assertTrue(mapData.hasAnyOfKeys(listOf("test", "f", "g", "a")))
    assertTrue(mapData.hasAnyOfKeys(listOf("c", "f")))
    assertFalse(mapData.hasAnyOfKeys(listOf("d", "e")))
    assertFalse(listData.hasAnyOfKeys(listOf("1", "a", "0")))
  }

  @Test
  fun `should correctly get the map size`() {
    assertEquals(3, mapData.mapSize())
    assertEquals(0, listData.mapSize())
    assertEquals(0, stringData.mapSize())
    assertEquals(0, intData.mapSize())
  }

  @Test
  fun `should create child AnyServerDrivenData according to key`() {
    assertEquals(mapValue["a"], mapData.get("a").asInt())
    assertEquals(mapValue["b"], mapData.get("b").asInt())
    assertEquals(mapValue["c"], mapData.get("c").asIntOrNull())
    assertEquals(null, mapData.get("d").asIntOrNull())
    assertFalse(mapData.hasError())
    mapData.get("d").asInt()
    assertTrue(mapData.hasError())
    assertContains(mapData.errorsAsString(), error("a number", "null", "d"))
  }

  @Test
  fun `should create child AnyServerDrivenData at key even if the content is not a map`() {
    assertEquals(null, stringData.get("a").asIntOrNull())
    assertEquals(null, booleanData.get("a").asIntOrNull())
    assertEquals(null, intData.get("a").asIntOrNull())
    assertEquals(null, longData.get("a").asIntOrNull())
    assertEquals(null, floatData.get("a").asIntOrNull())
    assertEquals(null, doubleData.get("a").asIntOrNull())
    assertEquals(null, listData.get("a").asIntOrNull())
    assertEquals(null, eventData.get("a").asIntOrNull())
    assertFalse(mapData.hasError())
    stringData.get("a").asInt()
    assertTrue(stringData.hasError())
    assertContains(stringData.errorsAsString(), error("a number", "null", "a"))
  }
}
