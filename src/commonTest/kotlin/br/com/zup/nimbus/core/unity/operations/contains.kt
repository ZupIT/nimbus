package br.com.zup.nimbus.core.unity.operations

import br.com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private val contains = coreUILibrary.getOperation("contains")!!

class ContainsOperationTest {
  private val list = listOf("one", "two", "three")

  @Test
  fun `should return true when there is the required item on a list`() {
    val result = contains(listOf(list, "two")) as Boolean
    assertTrue { result }
  }

  @Test
  fun `should return false when there is not the required item on a list`() {
    val result = contains(listOf(list, "four")) as Boolean
    assertFalse { result }
  }

  @Test
  fun `should be able to find a more complex object`() {
    val findableItem = mapOf("a" to "x", "b" to "y", "c" to "z")
    val complexList = listOf(mapOf("a" to "x"), mapOf("b" to "y", "c" to "z"), findableItem, mapOf("c" to "z"))
    val result = contains(listOf(complexList, findableItem)) as Boolean
    assertTrue { result }
  }

  @Test
  fun `should return true when there is the term inside a string`() {
    val result = contains(listOf("This is the test string with more words", "test")) as Boolean
    assertTrue { result }
  }

  @Test
  fun `should return false when there is not the term inside a string`() {
    val result = contains(listOf("This is the test string with more words", "strawberry")) as Boolean
    assertFalse { result }
  }
}
