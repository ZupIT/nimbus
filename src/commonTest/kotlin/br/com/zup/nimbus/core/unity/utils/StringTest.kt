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

import br.com.zup.nimbus.core.utils.addPrefix
import br.com.zup.nimbus.core.utils.removePrefix
import br.com.zup.nimbus.core.utils.removeSuffix
import kotlin.test.Test
import kotlin.test.assertEquals

class StringTest {
  @Test
  fun `should remove the prefix of a string`() {
    val original = "This is my Test"
    val result = removePrefix(original, "This is my ")
    assertEquals("Test", result)
  }

  @Test
  fun `should add a prefix in a string`() {
    val original = "Test"
    val result = addPrefix(original, "This is my ")
    assertEquals("This is my Test", result)
  }

  @Test
  fun `should not add a prefix in a string when the prefix is equal to the first char`() {
    val original = "Test"
    val result = addPrefix(original, "T")
    assertEquals("Test", result)
  }

  @Test
  fun `should remove the suffix of a string`() {
    val original = "Test content"
    val result = removeSuffix(original, " content")
    assertEquals("Test", result)
  }
}
