package br.com.zup.nimbus.core.unity.deserialization

import br.com.zup.nimbus.core.deserialization.AnyServerDrivenData
import kotlin.test.Test

class EventTest: AnyServerDrivenDataTest() {
  private fun errors() = mapOf(
    stringData to error("an event", "string"),
    booleanData to error("an event", "boolean"),
    intData to error("an event", "int"),
    longData to error("an event", "long"),
    floatData to error("an event", "float"),
    doubleData to error("an event", "double"),
    mapData to error("an event", "map"),
    listData to error("an event", "list"),
    enumData to error("an event", "string"),
  )

  private fun shouldDeserialize(
    expectedNull: Any?,
    additionalErrors: Map<AnyServerDrivenData, String> = emptyMap(),
    deserialize: (AnyServerDrivenData) -> Any?,
  ) {
    deserializeAndCheckResult(
      expectedString = AnyServerDrivenData.emptyEvent,
      expectedInt = AnyServerDrivenData.emptyEvent,
      expectedDouble = AnyServerDrivenData.emptyEvent,
      expectedFloat = AnyServerDrivenData.emptyEvent,
      expectedLong = AnyServerDrivenData.emptyEvent,
      expectedBoolean = AnyServerDrivenData.emptyEvent,
      expectedNull = expectedNull,
      expectedMap = AnyServerDrivenData.emptyEvent,
      expectedList = AnyServerDrivenData.emptyEvent,
      expectedEvent = eventValue,
      expectedEnum = AnyServerDrivenData.emptyEvent,
      deserialize = deserialize,
    )
    checkErrors(errors() + additionalErrors)
  }

  @Test
  fun `should deserialize using asBooleanOrNull`() = shouldDeserialize(null) { it.asEventOrNull() }

  @Test
  fun `should deserialize using asBoolean`() = shouldDeserialize(
    AnyServerDrivenData.emptyEvent,
    mapOf(nullData to error("an event", "null")),
  ) { it.asEvent() }

  @Test
  fun `should correctly identify if the content of the AnyServerDrivenData is an event`() =
    checkType(typeName = "an event", expectedMatch = eventData) { it.isEvent() }
}
