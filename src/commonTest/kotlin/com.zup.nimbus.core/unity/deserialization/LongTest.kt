package com.zup.nimbus.core.unity.deserialization

import com.zup.nimbus.core.deserialization.AnyServerDrivenData
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
      expectedEnum = AnyServerDrivenData.emptyLong,
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
  fun `should correctly identify if the content of the AnyServerDrivenData is a long`() =
    checkType(typeName = "a long", expectedMatch = longData) { it.isLong() }

  @Test
  fun `should be able to deserialize strings with both the minimum and maximum long`() {
    assertEquals(Long.MIN_VALUE, AnyServerDrivenData("${Long.MIN_VALUE}").asLong())
    assertEquals(Long.MAX_VALUE, AnyServerDrivenData("${Long.MAX_VALUE}").asLong())
  }

  private fun validateBounds(value: String) {
    val data = AnyServerDrivenData(value)
    data.asLongOrNull()
    data.asLong()
    assertTrue(data.hasError())
    assertEquals(3, data.errorsAsString(">").split(">").size)
    assertContains(data.errorsAsString(), error("a number", "string"), true)
  }

  @Test
  fun `should fail to deserialize string with number greater than the maximum Long value`() =
    validateBounds("${Long.MAX_VALUE}9")

  @Test
  fun `should fail to deserialize string with number lower than the minimum Long value`() =
    validateBounds("${Long.MIN_VALUE}9")
}
