/*
 * Copyright 2023 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.zup.nimbus.core.unity.deserialization

import br.com.zup.nimbus.core.deserialization.AnyServerDrivenData
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
