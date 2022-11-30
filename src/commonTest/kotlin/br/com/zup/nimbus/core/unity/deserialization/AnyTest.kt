package br.com.zup.nimbus.core.unity.deserialization

import br.com.zup.nimbus.core.deserialization.AnyServerDrivenData
import kotlin.test.Test

class AnyTest: AnyServerDrivenDataTest() {
  private fun shouldDeserialize(
    expectedNull: Any?,
    errors: Map<AnyServerDrivenData, String> = emptyMap(),
    deserialize: (AnyServerDrivenData) -> Any?,
  ) {
    deserializeAndCheckResult(
      expectedString = stringValue,
      expectedInt = intValue,
      expectedDouble = doubleValue,
      expectedFloat = floatValue,
      expectedLong = longValue,
      expectedBoolean = booleanValue,
      expectedNull = expectedNull,
      expectedMap = mapValue,
      expectedList = listValue,
      expectedEvent = eventValue,
      expectedEnum = enumValue,
      deserialize = deserialize,
    )
    checkErrors(errors)
  }

  @Test
  fun `should deserialize using asAnyOrNull`() = shouldDeserialize(null) { it.asAnyOrNull() }

  @Test
  fun `should deserialize using asAny`() = shouldDeserialize(
    AnyServerDrivenData.emptyAny,
    mapOf(nullData to error("anything", "null")),
  ) { it.asAny() }
}
