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

import br.com.zup.nimbus.core.utils.then
import kotlin.test.Test
import kotlin.test.assertEquals

class BooleanTest {
  private var mockValue = "Test"

  @Test
  fun `should return the first value when the condition is true`() {
    val result = ((mockValue == "Test") then 1) ?: 2
    assertEquals(1, result)
  }

  @Test
  fun `should return the second value when the condition is false`() {
    mockValue = "Tset"
    val result = ((mockValue == "Test") then 1) ?: 2
    assertEquals(2, result)
  }
}
