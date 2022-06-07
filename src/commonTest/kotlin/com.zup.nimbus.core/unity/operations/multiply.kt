package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.multiply
import kotlin.test.Test
import kotlin.test.assertEquals

class MultiplyOperationTest {
  @Test
  fun `should multiply correctly`() {
    val result = multiply(arrayOf(16.0, 2.0, 2.0))
    assertEquals(64.0, result)
  }
}
