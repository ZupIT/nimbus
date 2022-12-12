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

private val isEmpty = coreUILibrary.getOperation("isEmpty")!!

class IsEmptyOperationTest {
  @Test
  fun `should return true when value is null`() {
    assertTrue { isEmpty(listOf(null)) as Boolean }
  }

  @Test
  fun `should verify if an Array is empty`() {
    assertTrue { isEmpty(listOf(listOf<Any>())) as Boolean }
  }

  @Test
  fun `should verify if an List is empty`() {
    assertTrue { isEmpty(listOf(listOf<Any>())) as Boolean }
  }

  @Test
  fun `should verify if an Map is empty`() {
    assertTrue { isEmpty(listOf(mapOf<String, Boolean>())) as Boolean }
  }

  @Test
  fun `should verify if an String is empty`() {
    assertTrue { isEmpty(listOf("")) as Boolean }
  }

  @Test
  fun `should return false even with an empty object`() {
    assertFalse { isEmpty(listOf(object { })) as Boolean }
    assertFalse { isEmpty(listOf(object { val hello = "world" })) as Boolean }
  }

  @Test
  fun `should return false with any other type that can not be empty`() {
    assertFalse { isEmpty(listOf(0)) as Boolean }
    assertFalse { isEmpty(listOf(432321.2333)) as Boolean }
    assertFalse { isEmpty(listOf(false)) as Boolean }
    assertFalse { isEmpty(listOf(true)) as Boolean }
  }
}
