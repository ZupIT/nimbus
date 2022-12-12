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

package br.com.zup.nimbus.core.unity.operations

import br.com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private val lt = coreUILibrary.getOperation("lt")!!

class LtOperationTest {
  @Test
  fun `should compare two numbers and tell that a is lesser than b`() {
    assertTrue { lt(listOf(4, 5)) as Boolean }
    assertTrue { lt(listOf(5.4566, 5.4567)) as Boolean }
    assertFalse { lt(listOf(5.4566, 5)) as Boolean }
  }

  @Test
  fun `should ignore data type when comparing - type coercion`() {
    // Given
    val operations = listOf<Pair<Any, Any>>(
      2 to 1,
      1 to 1,
      1 to 2,
      2.0 to 1.0,
      2.0 to 1,
      1.0 to 1,
      1.0 to 2,

      "2" to 1.0,
      "2" to 1,
      "2" to "1",
      "1" to "1",
      "1" to "1.0",
      "1.0" to 2.0,
      "1.0" to "2.0",
      "true" to 2,
    )

    // When
    val result = operations.map {
      lt(listOf(it.first, it.second)) as Boolean
    }

    // Then
    val expected = listOf(
      false,
      false,
      true,
      false,
      false,
      false,
      true,
      false,
      false,
      false,
      false,
      false,
      true,
      true,
      false
    )

    assertEquals(expected, result)
  }
}
