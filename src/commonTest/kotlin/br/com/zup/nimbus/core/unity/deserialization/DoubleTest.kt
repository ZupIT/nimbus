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
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DoubleTest: AnyServerDrivenDataTest() {
  private fun shouldDeserialize(
    expectedNull: Any?,
    additionalErrors: Map<AnyServerDrivenData, String> = emptyMap(),
    deserialize: (AnyServerDrivenData) -> Any?,
  ) {
    deserializeAndCheckResult(
      expectedString = stringValue.toDouble(),
      expectedInt = intValue.toDouble(),
      expectedDouble = doubleValue,
      expectedFloat = floatValue.toDouble(),
      expectedLong = longValue.toDouble(),
      expectedBoolean = AnyServerDrivenData.emptyDouble,
      expectedNull = expectedNull,
      expectedMap = AnyServerDrivenData.emptyDouble,
      expectedList = AnyServerDrivenData.emptyDouble,
      expectedEvent = AnyServerDrivenData.emptyDouble,
      expectedEnum = AnyServerDrivenData.emptyDouble,
      deserialize = deserialize,
    )
    checkErrors(numberErrors() + additionalErrors)
  }

  @Test
  fun `should deserialize using asDoubleOrNull`() = shouldDeserialize(null) { it.asDoubleOrNull() }

  @Test
  fun `should deserialize using asDouble`() = shouldDeserialize(
    AnyServerDrivenData.emptyDouble,
    mapOf(nullData to error("a number", "null")),
  ) { it.asDouble() }

  @Test
  fun `should correctly identify if the content of the AnyServerDrivenData is a double`() =
    checkType(typeName = "a double", expectedMatch = doubleData) { it.isDouble() }

  @Test
  fun `should be able to deserialize strings with both the minimum and maximum double`() {
    assertEquals(Double.MIN_VALUE, AnyServerDrivenData("${Double.MIN_VALUE}").asDouble())
    assertEquals(Double.MAX_VALUE, AnyServerDrivenData("${Double.MAX_VALUE}").asDouble())
  }

  @Test
  fun `should truncate numerical strings that won't fit a Double`() {
    assertEquals(1.0123456789012346, AnyServerDrivenData("1.01234567890123456789").asDouble())
    assertEquals(-1.0123456789012346, AnyServerDrivenData("-1.01234567890123456789").asDouble())
  }

  @Test
  fun `should assume infinity for positive integer strings that won't fit a Double`() {
    assertEquals(Double.POSITIVE_INFINITY, AnyServerDrivenData("${Double.MAX_VALUE}9").asDouble())
  }

  @Test
  fun `should assume zero for negative integer strings that won't fit a Double`() {
    assertEquals(0.0, AnyServerDrivenData("${Double.MIN_VALUE}9").asDouble())
  }
}
