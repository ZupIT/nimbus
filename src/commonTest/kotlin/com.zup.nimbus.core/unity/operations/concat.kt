package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.getOtherOperations
import kotlin.test.Test
import kotlin.test.assertEquals

private val concat = getOtherOperations()["concat"]!!

class ConcatOperationTest {
  @Test
  fun `should concat all the string into a single one`() {
    val result = concat(listOf("one", "-two-", "three"))
    assertEquals("one-two-three", result)
  }

  @Test
  fun `should merge multiple lists into one in the order of the arguments`() {
    val a = listOf(1, 2, 3)
    val b = listOf("a", "b", "c")
    val c = listOf(1, true, "c")
    val result = concat(listOf(a, b, c)) as List<Any>

    assertEquals(9, result.size)
    assertEquals(1, result[0])
    assertEquals(2, result[1])
    assertEquals(3, result[2])
    assertEquals("a", result[3])
    assertEquals("b", result[4])
    assertEquals("c", result[5])
    assertEquals(1, result[6])
    assertEquals(true, result[7])
    assertEquals("c", result[8])
  }

  // TODO: concat maps
}
