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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private val or = coreUILibrary.getOperation("or")!!

class OrOperationTest {
  @Test
  fun `should return true when at least one condition is true`() {
    val a = 1
    val b = 2
    assertTrue { or(listOf(a > b, a == b, a < b)) as Boolean }
    assertTrue { or(listOf(a < b)) as Boolean }
    assertFalse { or(listOf(a == b)) as Boolean }
    assertFalse { or(listOf(a > b, a == b)) as Boolean }
  }
}
