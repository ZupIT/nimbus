package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.union
import kotlin.test.Test
import kotlin.test.assertEquals

class UnionOperationTest {
  @Test
  fun `should merge multiple lists into one in the order of the arguments`() {
    val a = mutableListOf<Any?>(1, 2, 3)
    val b = mutableListOf<Any?>("a", "b", "c")
    val c = mutableListOf<Any?>(1, true, "c")
    val result = union(a, b, c)

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
}
