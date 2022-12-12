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

private val divide = coreUILibrary.getOperation("divide")!!

class DivideOperationTest {
  @Test
  fun `should divide correctly integers`() {
    val result = divide(listOf(16, 2, 2))
    assertEquals(4, result)
  }

  @Test
  fun `should divide correctly doubles`() {
    val result = divide(listOf(16.3, 2.4, 2.9))
    assertEquals(2.341954022988506, result)
  }

  @Test
  fun `should divide correctly mixed`() {
    val result = divide(listOf(16, 2.5, 2))
    assertEquals(3.2, result)
  }

  @Test
  fun `should divide despite the type of the data - type coercion`() {
    // Given
    val operations = listOf<Pair<Any, Any>>(
      6 to 4, 4.5 to 6, 4.5 to 4.5, 6 to 3.0,
      3 to 1.5, 2.0 to 1, "1" to 1.0, 2.5 to "1.0", "1" to "1", "2" to 1,
      1 to true, "1" to false, "" to ""
    )

    // When
    val result = operations.map {
      divide(listOf(it.first, it.second))
    }

    // Then
    val expected = listOf<Number?>(1.5, 0.75, 1.0, 2.0, 2.0, 2.0, 1.0, 2.5, 1, 2, null, null, null)
    assertEquals(expected, result)
  }
}
