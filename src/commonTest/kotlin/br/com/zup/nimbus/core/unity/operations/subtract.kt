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

private val subtract = coreUILibrary.getOperation("subtract")!!

class SubtractOperationTest {
  @Test
  fun `should subtract correctly integers`() {
    val result = subtract(listOf(16, 2, 2))
    assertEquals(12, result)
  }

  @Test
  fun `should subtract correctly doubles`() {
    val result = subtract(listOf(16.3, 2.4, 2.9))
    assertEquals(11.0, result)
  }

  @Test
  fun `should subtract correctly mixed`() {
    val result = subtract(listOf(16, 2.5, 2))
    assertEquals(11.5, result)
  }

  @Test
  fun `should subtract despite the type of the data - type coercion`() {
    // Given
    val operations = listOf<Pair<Any, Any>>(
      6 to 4, 4.5 to 6, 4.5 to 4.5, 6 to 4.5,
      1 to 1.5, 2.0 to 1, "1" to 1.0, 2.5 to "1.0", "1" to "1", "2" to 1,
      1 to true, "1" to false, "" to ""
    )

    // When
    val result = operations.map {
      subtract(listOf(it.first, it.second))
    }

    // Then
    val expected = listOf<Number?>(2, -1.5, 0.0, 1.5, -0.5, 1.0, 0.0, 1.5, 0, 1, null, null, null)

    assertEquals(expected, result)
  }
}
