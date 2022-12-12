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
