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

private val not = coreUILibrary.getOperation("not")!!

class NotOperationTest {
  @Test
  fun `should return true when a negative condition is passed`() {
    val a = 1
    val b = 2
    assertTrue { not(listOf(a > b)) as Boolean }
    assertFalse { not(listOf(a < b)) as Boolean }
  }
}
