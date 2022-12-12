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

private val multiply = coreUILibrary.getOperation("multiply")!!

class MultiplyOperationTest {
  @Test
  fun `should multiply correctly integers`() {
    val result = multiply(listOf(16, 2, 2))
    assertEquals(64, result)
  }

  @Test
  fun `should multiply correctly doubles`() {
    val result = multiply(listOf(16.3, 2.4, 2.9))
    assertEquals(113.448, result)
  }

  @Test
  fun `should multiply correctly mixed`() {
    val result = multiply(listOf(16, 2.5, 2))
    assertEquals(80.0, result)
  }

  @Test
  fun `should multiply despite the type of the data - type coercion`() {
    // Given
    val operations = listOf<Pair<Any, Any>>(
      6 to 4, 4.5 to 6, 4.5 to 4.5, 6 to 4.5,
      1 to 1.5, 2.0 to 1, "1" to 1.0, 2.5 to "1.0", "1" to "1", "2" to 1,
      1 to true, "1" to false, "" to ""
    )

    // When
    val result = operations.map {
      multiply(listOf(it.first, it.second))
    }

    // Then
    val expected = listOf<Number?>(24, 27.0, 20.25, 27.0, 1.5, 2.0, 1.0, 2.5, 1, 2, null, null, null)
    assertEquals(expected, result)
  }
}
