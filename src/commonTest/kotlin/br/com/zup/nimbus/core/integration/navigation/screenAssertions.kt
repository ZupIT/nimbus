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

package br.com.zup.nimbus.core.integration.navigation

import br.com.zup.nimbus.core.tree.ServerDrivenEvent
import br.com.zup.nimbus.core.tree.ServerDrivenNode
import kotlin.test.assertEquals

fun verifyScreen1(tree: ServerDrivenNode?) {
  assertEquals(false, tree == null)
  assertEquals("layout:container", tree?.component)
  assertEquals(true, tree?.id?.isNotBlank())
  assertEquals(2, tree?.children?.size)
  val text = tree?.children?.get(0)
  assertEquals("material:text", text?.component)
  assertEquals(true, text?.id?.isNotBlank())
  assertEquals("Screen 1", text?.properties?.get("text"))
  val button = tree?.children?.get(1)
  assertEquals("material:button", button?.component)
  assertEquals(true, button?.id?.isNotBlank())
  assertEquals("Next", button?.properties?.get("text"))
  assertEquals(true, button?.properties?.get("onPress") is ServerDrivenEvent)
}

fun verifyScreen2(tree: ServerDrivenNode?) {
  assertEquals(false, tree == null)
  assertEquals("layout:container", tree?.component)
  assertEquals(true, tree?.id?.isNotBlank())
  assertEquals(3, tree?.children?.size)
  val text = tree?.children?.get(0)
  assertEquals("material:text", text?.component)
  assertEquals(true, text?.id?.isNotBlank())
  assertEquals("Screen 2", text?.properties?.get("text"))
  val nextButton = tree?.children?.get(1)
  assertEquals("material:button", nextButton?.component)
  assertEquals(true, nextButton?.id?.isNotBlank())
  assertEquals("Next", nextButton?.properties?.get("text"))
  assertEquals(true, nextButton?.properties?.get("onPress") is ServerDrivenEvent)
  val previousButton = tree?.children?.get(2)
  assertEquals("material:button", previousButton?.component)
  assertEquals(true, previousButton?.id?.isNotBlank())
  assertEquals("Previous", previousButton?.properties?.get("text"))
  assertEquals(true, previousButton?.properties?.get("onPress") is ServerDrivenEvent)
}

fun verifyScreen3(tree: ServerDrivenNode?) {
  assertEquals(false, tree == null)
  assertEquals("layout:container", tree?.component)
  assertEquals(true, tree?.id?.isNotBlank())
  assertEquals(4, tree?.children?.size)
  val text = tree?.children?.get(0)
  assertEquals("material:text", text?.component)
  assertEquals(true, text?.id?.isNotBlank())
  assertEquals("Screen 3", text?.properties?.get("text"))
  val nextButtonFallback = tree?.children?.get(1)
  assertEquals("material:button", nextButtonFallback?.component)
  assertEquals(true, nextButtonFallback?.id?.isNotBlank())
  assertEquals("Next (error with fallback)", nextButtonFallback?.properties?.get("text"))
  assertEquals(true, nextButtonFallback?.properties?.get("onPress") is ServerDrivenEvent)
  val nextButtonException = tree?.children?.get(2)
  assertEquals("material:button", nextButtonException?.component)
  assertEquals(true, nextButtonException?.id?.isNotBlank())
  assertEquals("Next (error without fallback)", nextButtonException?.properties?.get("text"))
  assertEquals(true, nextButtonException?.properties?.get("onPress") is ServerDrivenEvent)
  val previousButton = tree?.children?.get(3)
  assertEquals("material:button", previousButton?.component)
  assertEquals(true, previousButton?.id?.isNotBlank())
  assertEquals("Previous", previousButton?.properties?.get("text"))
  assertEquals(true, previousButton?.properties?.get("onPress") is ServerDrivenEvent)
}

fun verifyFallbackScreen(tree: ServerDrivenNode?) {
  assertEquals(false, tree == null)
  assertEquals("layout:container", tree?.component)
  assertEquals(true, tree?.id?.isNotBlank())
  assertEquals(2, tree?.children?.size)
  val text = tree?.children?.get(0)
  assertEquals("material:text", text?.component)
  assertEquals(true, text?.id?.isNotBlank())
  assertEquals("Error fallback", text?.properties?.get("text"))
  val button = tree?.children?.get(1)
  assertEquals("material:button", button?.component)
  assertEquals(true, button?.id?.isNotBlank())
  assertEquals("Back to main flow", button?.properties?.get("text"))
  assertEquals(true, button?.properties?.get("onPress") is ServerDrivenEvent)
}
