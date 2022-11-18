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
      expectedEnum = AnyServerDrivenData.emptyFloat,
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
  fun `should correctly identify if the content of the AnyServerDrivenData is a float`() =
    checkType(typeName = "a float", expectedMatch = floatData) { it.isFloat() }

  @Test
  fun `should be able to deserialize strings with both the minimum and maximum float`() {
    assertEquals(Float.MIN_VALUE, AnyServerDrivenData("${Float.MIN_VALUE}").asFloat())
    assertEquals(Float.MAX_VALUE, AnyServerDrivenData("${Float.MAX_VALUE}").asFloat())
  }

  @Test
  fun `should truncate Double that won't fit a Float`() {
    assertEquals(1.0123457F, AnyServerDrivenData(1.01234567).asFloat())
    assertEquals(-1.0123457F, AnyServerDrivenData(-1.01234567).asFloat())
  }

  @Test
  fun `should truncate numerical strings that won't fit a Float`() {
    assertEquals(1.0123457F, AnyServerDrivenData("1.01234567890123456789").asFloat())
    assertEquals(-1.0123457F, AnyServerDrivenData("-1.01234567890123456789").asFloat())
  }

  @Test
  fun `should assume infinity for positive integer strings that won't fit a Float`() {
    assertEquals(Float.POSITIVE_INFINITY, AnyServerDrivenData("${Float.MAX_VALUE}9").asFloat())
  }

  @Test
  fun `should assume zero for negative integer strings that won't fit a Float`() {
    assertEquals(0F, AnyServerDrivenData("${Float.MIN_VALUE}9").asFloat())
  }
}
