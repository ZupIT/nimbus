package com.zup.nimbus.core.unity.deserialization

import com.zup.nimbus.core.deserialization.AnyServerDrivenData
import kotlin.test.Test

class EnumTest: AnyServerDrivenDataTest() {
  private fun errors() = mapOf(
    stringData to error("Cat, Dog, Cow", stringValue),
    intData to error("Cat, Dog, Cow", "$intValue"),
    longData to error("Cat, Dog, Cow", "$longValue"),
    floatData to error("Cat, Dog, Cow", "$floatValue"),
    doubleData to error("Cat, Dog, Cow", "$doubleValue"),
    mapData to error("Cat, Dog, Cow", "$mapValue"),
    listData to error("Cat, Dog, Cow", "$listValue"),
    eventData to error("Cat, Dog, Cow", "$eventValue"),
    booleanData to error("Cat, Dog, Cow", "$booleanValue"),
  )

  private fun shouldDeserialize(
    expectedNull: Any?,
    additionalError: Map<AnyServerDrivenData, String> = emptyMap(),
    deserialize: (AnyServerDrivenData) -> Any?,
  ) {
    deserializeAndCheckResult(
      expectedString = Animal.Cat,
      expectedInt = Animal.Cat,
      expectedDouble = Animal.Cat,
      expectedFloat = Animal.Cat,
      expectedLong = Animal.Cat,
      expectedBoolean = Animal.Cat,
      expectedNull = expectedNull,
      expectedMap = Animal.Cat,
      expectedList = Animal.Cat,
      expectedEvent = Animal.Cat,
      expectedEnum = Animal.Dog,
      deserialize = deserialize,
    )
    checkErrors(errors() + additionalError)
  }

  @Test
  fun `should deserialize using asEnumOrNull`() = shouldDeserialize(null) {
    it.asEnumOrNull(Animal.values())
  }

  @Test
  fun `should deserialize using asEnum`() = shouldDeserialize(
    Animal.Cat,
    mapOf(nullData to error("Cat, Dog, Cow", "null")),
  ) { it.asEnum(Animal.values()) }
}
