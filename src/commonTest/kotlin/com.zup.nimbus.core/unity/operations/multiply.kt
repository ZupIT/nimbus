package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.multiply
import kotlin.test.Test
import kotlin.test.assertEquals

class MultiplyOperationTest {
  @Test
  fun `should multiply correctly integers`() {
    val result = multiply(arrayOf(16, 2, 2))
    assertEquals(64, result)
  }

  @Test
  fun `should multiply correctly doubles`() {
    val result = multiply(arrayOf(16.3, 2.4, 2.9))
    assertEquals(113.448, result)
  }

  @Test
  fun `should multiply correctly mixed`() {
    val result = multiply(arrayOf(16, 2.5, 2))
    assertEquals(80.0, result)
  }
}
