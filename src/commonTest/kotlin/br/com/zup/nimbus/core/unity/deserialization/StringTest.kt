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
