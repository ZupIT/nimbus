package com.zup.nimbus.core.unity.utils

import com.zup.nimbus.core.utils.mapValuesToMutableList
import kotlin.test.Test
import kotlin.test.assertEquals

class ListTest {
  @Test
  fun `should transform a list to a mutable list using an iteratee for each item of the list`() {
    val baseList = listOf("one", "two", "three", "four")
    var iterations = 0
    val testIteratee = fun (item: String): String {
      iterations++
      return "${item}-${iterations}"
    }
    val result = mapValuesToMutableList(baseList, testIteratee)
    assertEquals(4, result.size)
    assertEquals(4, iterations)
    assertEquals("one-1", result[0])
    assertEquals("two-2", result[1])
    assertEquals("three-3", result[2])
    assertEquals("four-4", result[3])
  }
}
