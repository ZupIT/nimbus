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

private val length = coreUILibrary.getOperation("length")!!

class LengthOperationTest {
  @Test
  fun `should return the correct length of a list`() {
    var list = listOf(1,2,3,4,5)
    var result = length(listOf(list))
    assertEquals(5, result)

    list = emptyList()
    result = length(listOf(list))
    assertEquals(0, result)

    list = mutableListOf(1,2,3,4,5)
    result = length(listOf(list))
    assertEquals(5, result)
  }

  @Test
  fun `should return the correct length of a map`() {
    var map = mapOf(
      "one" to 1,
      "two" to 2,
      "three" to 3,
      "four" to 4,
      "five" to 5
    )
    var result = length(listOf(map))
    assertEquals(5, result)

    map = emptyMap<String, Int>()
    result = length(listOf(map))
    assertEquals(0, result)

    map = mutableMapOf(
      "one" to 1,
      "two" to 2,
      "three" to 3,
      "four" to 4,
      "five" to 5
    )
    result = length(listOf(map))
    assertEquals(5, result)
  }

  @Test
  fun `should return the correct length of a string`() {
    var string = "Test"
    var result = length(listOf(string))
    assertEquals(4, result)

    string = "This is a test"
    result = length(listOf(string))
    assertEquals(14, result)

    string = ""
    result = length(listOf(string))
    assertEquals(0, result)
  }
}
