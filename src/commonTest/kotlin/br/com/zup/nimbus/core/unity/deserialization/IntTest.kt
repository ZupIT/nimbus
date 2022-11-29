package br.com.zup.nimbus.core.unity.deserialization

import br.com.zup.nimbus.core.deserialization.AnyServerDrivenData
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
      expectedEnum = AnyServerDrivenData.emptyInt,
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
  fun `should correctly identify if the content of the AnyServerDrivenData is an int`() =
    checkType(typeName = "an int", expectedMatch = intData) { it.isInt() }

  @Test
  fun `should be able to deserialize strings with both the minimum and maximum int`() {
    assertEquals(Int.MIN_VALUE, AnyServerDrivenData("${Int.MIN_VALUE}").asInt())
    assertEquals(Int.MAX_VALUE, AnyServerDrivenData("${Int.MAX_VALUE}").asInt())
  }

  private fun validateBounds(value: String) {
    val data = AnyServerDrivenData(value)
    data.asIntOrNull()
    data.asInt()
    assertTrue(data.hasError())
    assertEquals(3, data.errorsAsString(">").split(">").size)
    assertContains(data.errorsAsString(), error("a number", "string"), true)
  }

  @Test
  fun `should fail to deserialize string with number greater than the maximum Int value`() =
    validateBounds("${Int.MAX_VALUE}9")

  @Test
  fun `should fail to deserialize string with number lower than the minimum Int value`() =
    validateBounds("${Int.MIN_VALUE}9")

  @Test
  fun `should use the maximum integer when the source is a Long greater than it and the minimum when it's less`() {
    assertEquals(Int.MAX_VALUE, AnyServerDrivenData(Int.MAX_VALUE + 1L).asInt())
    assertEquals(Int.MIN_VALUE, AnyServerDrivenData(Int.MIN_VALUE - 1L).asInt())
  }

  @Test
  fun `should use the maximum integer when the source is a Float greater than it and the minimum when it's less`() {
    assertEquals(Int.MAX_VALUE, AnyServerDrivenData(Int.MAX_VALUE + 1F).asInt())
    assertEquals(Int.MIN_VALUE, AnyServerDrivenData(Int.MIN_VALUE - 1F).asInt())
  }

  @Test
  fun `should use the maximum integer when the source is a Double greater than it and the minimum when it's less`() {
    assertEquals(Int.MAX_VALUE, AnyServerDrivenData(Int.MAX_VALUE + 1.0).asInt())
    assertEquals(Int.MIN_VALUE, AnyServerDrivenData(Int.MIN_VALUE - 1.0).asInt())
  }
}
