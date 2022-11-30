package br.com.zup.nimbus.core.unity.deserialization

import br.com.zup.nimbus.core.deserialization.AnyServerDrivenData
import kotlin.test.Test

class StringTest: AnyServerDrivenDataTest() {
  private fun shouldDeserialize(
    expectedNull: Any?,
    errors: Map<AnyServerDrivenData, String> = emptyMap(),
    deserialize: (AnyServerDrivenData) -> Any?,
  ) {
    deserializeAndCheckResult(
      expectedString = stringValue,
      expectedInt = "$intValue",
      expectedDouble = "$doubleValue",
      expectedFloat = "$floatValue",
      expectedLong = "$longValue",
      expectedBoolean = "$booleanValue",
      expectedNull = expectedNull,
      expectedMap = "$mapValue",
      expectedList = "$listValue",
      expectedEvent = "$eventValue",
      expectedEnum = enumValue,
      deserialize = deserialize,
    )
    checkErrors(errors)
  }

  @Test
  fun `should deserialize using asStringOrNull`() = shouldDeserialize(null) { it.asStringOrNull() }

  @Test
  fun `should deserialize using asString`() = shouldDeserialize(
    AnyServerDrivenData.emptyString,
    mapOf(nullData to error("a string", "null")),
  ) { it.asString() }

  @Test
  fun `should correctly identify if the content of the AnyServerDrivenData is a string`() =
    checkType(typeName = "a string", expectedMatches = listOf(stringData, enumData)) { it.isString() }
}
