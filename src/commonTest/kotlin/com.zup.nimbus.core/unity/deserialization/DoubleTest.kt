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
      expectedString = stringValue.toDouble(),
      expectedInt = intValue.toDouble(),
      expectedDouble = doubleValue,
      expectedFloat = floatValue.toDouble(),
      expectedLong = longValue.toDouble(),
      expectedBoolean = AnyServerDrivenData.emptyDouble,
      expectedNull = expectedNull,
      expectedMap = AnyServerDrivenData.emptyDouble,
      expectedList = AnyServerDrivenData.emptyDouble,
      expectedEvent = AnyServerDrivenData.emptyDouble,
      expectedEnum = AnyServerDrivenData.emptyDouble,
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
  fun `should correctly identify if the content of the AnyServerDrivenData is a double`() =
    checkType(typeName = "a double", expectedMatch = doubleData) { it.isDouble() }

  @Test
  fun `should be able to deserialize strings with both the minimum and maximum double`() {
    assertEquals(Double.MIN_VALUE, AnyServerDrivenData("${Double.MIN_VALUE}").asDouble())
    assertEquals(Double.MAX_VALUE, AnyServerDrivenData("${Double.MAX_VALUE}").asDouble())
  }

  @Test
  fun `should truncate numerical strings that won't fit a Double`() {
    assertEquals(1.0123456789012346, AnyServerDrivenData("1.01234567890123456789").asDouble())
    assertEquals(-1.0123456789012346, AnyServerDrivenData("-1.01234567890123456789").asDouble())
  }

  @Test
  fun `should assume infinity for positive integer strings that won't fit a Double`() {
    assertEquals(Double.POSITIVE_INFINITY, AnyServerDrivenData("${Double.MAX_VALUE}9").asDouble())
  }

  @Test
  fun `should assume zero for negative integer strings that won't fit a Double`() {
    assertEquals(0.0, AnyServerDrivenData("${Double.MIN_VALUE}9").asDouble())
  }
}
