package com.zup.nimbus.core.unity.deserialization

import com.zup.nimbus.core.deserialization.AnyServerDrivenData
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DoubleTest: AnyServerDrivenDataTest() {
  private fun shouldDeserialize(
    expectedNull: Any?,
    additionalErrors: Map<AnyServerDrivenData, String> = emptyMap(),
    deserialize: (AnyServerDrivenData) -> Any?,
  ) {
    deserializeAndCheckResult(
      expectedString = stringValue.toDouble().toDouble(),
      expectedInt = intValue.toDouble(),
      expectedDouble = doubleValue,
      expectedFloat = floatValue.toDouble(),
      expectedLong = longValue.toDouble(),
      expectedBoolean = AnyServerDrivenData.emptyDouble,
      expectedNull = expectedNull,
      expectedMap = AnyServerDrivenData.emptyDouble,
      expectedList = AnyServerDrivenData.emptyDouble,
      expectedEvent = AnyServerDrivenData.emptyDouble,
      deserialize = deserialize,
    )
    checkErrors(numberErrors() + additionalErrors)
  }

  @Test
  fun `should deserialize using asDoubleOrNull`() = shouldDeserialize(null) { it.asDoubleOrNull() }

  @Test
  fun `should deserialize using asDouble`() = shouldDeserialize(
    AnyServerDrivenData.emptyDouble,
    mapOf(nullData to error("a number", "null")),
  ) { it.asDouble() }

  @Test
  fun `should handle big number`() {
    assertEquals(12345678901.0123456789, AnyServerDrivenData("12345678901.0123456789").asDouble())
  }

  @Test
  fun `should correctly identify if the content of the AnyServerDrivenData is a double`() =
    checkType(typeName = "a double", expectedMatch = doubleData) { it.isDouble() }
}
