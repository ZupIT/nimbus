package com.zup.nimbus.core.unity.deserialization

import com.zup.nimbus.core.deserialization.AnyServerDrivenData
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FloatTest: AnyServerDrivenDataTest() {
  private fun shouldDeserialize(
    expectedNull: Any?,
    additionalErrors: Map<AnyServerDrivenData, String> = emptyMap(),
    deserialize: (AnyServerDrivenData) -> Any?,
  ) {
    deserializeAndCheckResult(
      expectedString = stringValue.toFloat(),
      expectedInt = intValue.toFloat(),
      expectedDouble = doubleValue.toFloat(),
      expectedFloat = floatValue,
      expectedLong = longValue.toFloat(),
      expectedBoolean = AnyServerDrivenData.emptyFloat,
      expectedNull = expectedNull,
      expectedMap = AnyServerDrivenData.emptyFloat,
      expectedList = AnyServerDrivenData.emptyFloat,
      expectedEvent = AnyServerDrivenData.emptyFloat,
      deserialize = deserialize,
    )
    checkErrors(numberErrors() + additionalErrors)
  }

  @Test
  fun `should deserialize using asFloatOrNull`() = shouldDeserialize(null) { it.asFloatOrNull() }

  @Test
  fun `should deserialize using asFloat`() = shouldDeserialize(
    AnyServerDrivenData.emptyFloat,
    mapOf(nullData to error("a number", "null")),
  ) { it.asFloat() }

  @Test
  fun `should truncate big number and lose decimal part`() {
    assertEquals(1.23456788E10F, AnyServerDrivenData("12345678901.0123456789").asFloat())
  }

  @Test
  fun `should correctly identify if the content of the AnyServerDrivenData is a float`() =
    checkType(typeName = "a float", expectedMatch = floatData) { it.isFloat() }
}
