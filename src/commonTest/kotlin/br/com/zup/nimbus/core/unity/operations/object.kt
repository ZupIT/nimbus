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

private val objectOperation = coreUILibrary.getOperation("object")!!

class ObjectOperationTest {
  @Test
  fun `should create object`() {
    val input = listOf(
      "key1", 1,
      "key2", 2,
      "key3", 3,
    )
    assertEquals(
      mapOf(
        "key1" to 1,
        "key2" to 2,
        "key3" to 3,
      ),
      objectOperation(input)
    )
  }

  @Test
  fun `should create object even if input has non-string keys`() {
    val input = listOf(
      true, 1,
      "key2", 2,
      5, "hello",
      58L, false,
      null, "test",
    )
    assertEquals(
      mapOf(
        "true" to 1,
        "key2" to 2,
        "5" to "hello",
        "58" to false,
        "null" to "test",
      ),
      objectOperation(input)
    )
  }

  @Test
  fun `should create object with odd number of arguments`() {
    val input = listOf(
      "key1", 1,
      "key2",
    )
    assertEquals(
      mapOf(
        "key1" to 1,
        "key2" to null,
      ),
      objectOperation(input)
    )
  }

  @Test
  fun `should use most recent value on repeated keys`() {
    val input = listOf(
      "key1", 1,
      "key1", 2,
      "key2", 3,
      "key2", 4,
    )
    assertEquals(
      mapOf(
        "key1" to 2,
        "key2" to 4,
      ),
      objectOperation(input)
    )
  }
}
