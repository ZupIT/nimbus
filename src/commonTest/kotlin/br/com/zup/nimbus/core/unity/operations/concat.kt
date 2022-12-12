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

private val concat = coreUILibrary.getOperation("concat")!!

class ConcatOperationTest {
  @Test
  fun `should concat all the string into a single one`() {
    val result = concat(listOf("one", "-two-", "three"))
    assertEquals("one-two-three", result)
  }

  @Test
  fun `should merge multiple lists into one in the order of the arguments`() {
    val a = listOf(1, 2, 3)
    val b = listOf("a", "b", "c")
    val c = listOf(1, true, "c")
    val result = concat(listOf(a, b, c)) as List<Any>

    assertEquals(9, result.size)
    assertEquals(1, result[0])
    assertEquals(2, result[1])
    assertEquals(3, result[2])
    assertEquals("a", result[3])
    assertEquals("b", result[4])
    assertEquals("c", result[5])
    assertEquals(1, result[6])
    assertEquals(true, result[7])
    assertEquals("c", result[8])
  }

  // TODO: concat maps
}
