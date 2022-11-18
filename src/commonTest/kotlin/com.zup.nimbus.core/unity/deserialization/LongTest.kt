package com.zup.nimbus.core.unity.deserialization

import com.zup.nimbus.core.deserialization.AnyServerDrivenData
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LongTest: AnyServerDrivenDataTest() {
  private fun shouldDeserialize(
    expectedNull: Any?,
    additionalErrors: Map<AnyServerDrivenData, String> = emptyMap(),
    deserialize: (AnyServerDrivenData) -> Any?,
  ) {
    deserializeAndCheckResult(
      expectedString = stringValue.toDouble().toLong(),
      expectedInt = intValue.toLong(),
      expectedDouble = doubleValue.toLong(),
      expectedFloat = floatValue.toLong(),
      expectedLong = longValue,
      expectedBoolean = AnyServerDrivenData.emptyLong,
      expectedNull = expectedNull,
      expectedMap = AnyServerDrivenData.emptyLong,
      expectedList = AnyServerDrivenData.emptyLong,
      expectedEvent = AnyServerDrivenData.emptyLong,
      deserialize = deserialize,
    )
    checkErrors(numberErrors() + additionalErrors)
  }

  @Test
  fun `should deserialize using asLongOrNull`() = shouldDeserialize(null) { it.asLongOrNull() }

  @Test
  fun `should deserialize using asLong`() = shouldDeserialize(
    AnyServerDrivenData.emptyLong,
    mapOf(nullData to error("a number", "null")),
  ) { it.asLong() }

  @Test
  fun `should deserialize big number truncating decimal part`() {
    assertEquals(12345678901, AnyServerDrivenData("12345678901.0123456789").asLong())
  }

  @Test
  fun `should correctly identify if the content of the AnyServerDrivenData is a long`() =
    checkType(typeName = "a long", expectedMatch = longData) { it.isLong() }
}
