package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.contains
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ContainsOperationTest {
  private val list = listOf("one", "two", "three")

  @Test
  fun `should return true when there is the required item on a list`() {
    val result = contains(arrayOf(list, "two")) as Boolean
    assertTrue { result }
  }

  @Test
  fun `should return false when there is not the required item on a list`() {
    val result = contains(arrayOf(list, "four")) as Boolean
    assertFalse { result }
  }

  @Test
  fun `should be able to find a more complex object`() {
    val findableItem = mapOf("a" to "x", "b" to "y", "c" to "z")
    val complexList = listOf(mapOf("a" to "x"), mapOf("b" to "y", "c" to "z"), findableItem, mapOf("c" to "z"))
    val result = contains(arrayOf(complexList, findableItem)) as Boolean
    assertTrue { result }
  }

  @Test
  fun `should return true when there is the term inside a string`() {
    val result = contains(arrayOf("This is the test string with more words", "test")) as Boolean
    assertTrue { result }
  }

  @Test
  fun `should return false when there is not the term inside a string`() {
    val result = contains(arrayOf("This is the test string with more words", "strawberry")) as Boolean
    assertFalse { result }
  }
}
