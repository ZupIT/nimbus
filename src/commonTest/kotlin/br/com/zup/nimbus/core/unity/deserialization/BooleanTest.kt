package br.com.zup.nimbus.core.unity.deserialization

import br.com.zup.nimbus.core.deserialization.AnyServerDrivenData
import kotlin.test.Test

class BooleanTest: AnyServerDrivenDataTest() {
  private fun errors() = mapOf(
    stringData to error("a boolean", "string"),
    intData to error("a boolean", "int"),
    longData to error("a boolean", "long"),
    floatData to error("a boolean", "float"),
    doubleData to error("a boolean", "double"),
    mapData to error("a boolean", "map"),
    listData to error("a boolean", "list"),
    eventData to error("a boolean", "event"),
    enumData to error("a boolean", "string"),
  )

  private fun shouldDeserialize(
    expectedNull: Any?,
    additionalErrors: Map<AnyServerDrivenData, String> = emptyMap(),
    deserialize: (AnyServerDrivenData) -> Any?,
  ) {
    deserializeAndCheckResult(
      expectedString = AnyServerDrivenData.emptyBoolean,
      expectedInt = AnyServerDrivenData.emptyBoolean,
      expectedDouble = AnyServerDrivenData.emptyBoolean,
      expectedFloat = AnyServerDrivenData.emptyBoolean,
      expectedLong = AnyServerDrivenData.emptyBoolean,
      expectedBoolean = booleanValue,
      expectedNull = expectedNull,
      expectedMap = AnyServerDrivenData.emptyBoolean,
      expectedList = AnyServerDrivenData.emptyBoolean,
      expectedEvent = AnyServerDrivenData.emptyBoolean,
      expectedEnum = AnyServerDrivenData.emptyBoolean,
      deserialize = deserialize,
    )
    checkErrors(errors() + additionalErrors)
  }

  @Test
  fun `should deserialize using asBooleanOrNull`() = shouldDeserialize(null) { it.asBooleanOrNull() }

  @Test
  fun `should deserialize using asBoolean`() = shouldDeserialize(
    AnyServerDrivenData.emptyBoolean,
    mapOf(nullData to error("a boolean", "null")),
  ) { it.asBoolean() }

  @Test
  fun `should correctly identify if the content of the AnyServerDrivenData is a boolean`() =
    checkType(typeName = "a boolean", expectedMatch = booleanData) { it.isBoolean() }
}
