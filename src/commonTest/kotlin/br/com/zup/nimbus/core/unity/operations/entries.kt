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

private val entries = coreUILibrary.getOperation("entries")!!

class EntriesOperationTest {
  @Test
  fun `should get entries of map`() {
    val map = mapOf("hello" to "world", "a" to 1, "b" to true, "c" to listOf("a", "b"), "d" to mapOf("e" to 10),
      "e" to null)
    assertEquals(listOf(
      mapOf("key" to "hello", "value" to "world"),
      mapOf("key" to "a", "value" to 1),
      mapOf("key" to "b", "value" to true),
      mapOf("key" to "c", "value" to listOf("a", "b")),
      mapOf("key" to "d", "value" to mapOf("e" to 10)),
      mapOf("key" to "e", "value" to null),
    ), entries(listOf(map)))
  }

  @Test
  fun `should get empty list if not a map`() {
    assertEquals(entries(listOf("hello")), emptyList<Any>())
  }
}
