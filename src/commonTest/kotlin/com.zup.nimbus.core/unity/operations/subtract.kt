package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.getNumberOperations
import kotlin.test.Test
import kotlin.test.assertEquals

private val subtract = getNumberOperations()["subtract"]!!

class SubtractOperationTest {
  @Test
  fun `should subtract correctly integers`() {
    val result = subtract(arrayOf(16, 2, 2))
    assertEquals(12, result)
  }

  @Test
  fun `should subtract correctly doubles`() {
    val result = subtract(arrayOf(16.3, 2.4, 2.9))
    assertEquals(11.0, result)
  }

  @Test
  fun `should subtract correctly mixed`() {
    val result = subtract(arrayOf(16, 2.5, 2))
    assertEquals(11.5, result)
  }
}
