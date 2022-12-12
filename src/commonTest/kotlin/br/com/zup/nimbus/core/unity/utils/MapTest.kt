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

package br.com.zup.nimbus.core.unity.utils

import br.com.zup.nimbus.core.utils.mapValuesToMutableMap
import kotlin.test.Test
import kotlin.test.assertEquals

class MapTest {
  @Test
  fun `should transform a map to a mutable map using an iteratee for each item of the map`() {
    val baseMap = mapOf(
      "one" to "the number is: ",
      "two" to "the number is: ",
      "three" to "the number is: ",
      "four" to "the number is: "
    )
    var iterations = 0
    val testIteratee = fun (item: Map.Entry<String, String>): Map.Entry<String, String> {
      iterations++
      return mapOf(item.key to "${item.value}${iterations}").entries.first()
    }
    val result = mapValuesToMutableMap(baseMap, testIteratee)
    assertEquals(4, result.size)
    assertEquals(4, iterations)
    assertEquals("the number is: 1", result["one"]?.value)
    assertEquals("the number is: 2", result["two"]?.value)
    assertEquals("the number is: 3", result["three"]?.value)
    assertEquals("the number is: 4", result["four"]?.value)
  }
}
