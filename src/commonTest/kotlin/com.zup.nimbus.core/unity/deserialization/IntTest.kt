package com.zup.nimbus.core.unity.deserialization

import com.zup.nimbus.core.deserialization.AnyServerDrivenData
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IntTest: AnyServerDrivenDataTest() {
  private fun shouldDeserialize(
    expectedNull: Any?,
    additionalErrors: Map<AnyServerDrivenData, String> = emptyMap(),
    deserialize: (AnyServerDrivenData) -> Any?,
  ) {
    deserializeAndCheckResult(
      expectedString = stringValue.toDouble().toInt(),
      expectedInt = intValue,
      expectedDouble = doubleValue.toInt(),
      expectedFloat = floatValue.toInt(),
      expectedLong = longValue.toInt(),
      expectedBoolean = AnyServerDrivenData.emptyInt,
      expectedNull = expectedNull,
      expectedMap = AnyServerDrivenData.emptyInt,
      expectedList = AnyServerDrivenData.emptyInt,
      expectedEvent = AnyServerDrivenData.emptyInt,
      deserialize = deserialize,
    )
    checkErrors(numberErrors() + additionalErrors)
  }

  @Test
  fun `should deserialize using asIntOrNull`() = shouldDeserialize(null) { it.asIntOrNull() }

  @Test
  fun `should deserialize using asInt`() = shouldDeserialize(
    AnyServerDrivenData.emptyInt,
    mapOf(nullData to error("a number", "null")),
  ) { it.asInt() }

  @Test
  fun `should assume max value on big number`() {
    assertEquals(Int.MAX_VALUE, AnyServerDrivenData("12345678901.0123456789").asInt())
  }

  @Test
  fun `should correctly identify if the content of the AnyServerDrivenData is an int`() =
    checkType(typeName = "an int", expectedMatch = intData) { it.isInt() }
}
