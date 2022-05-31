package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.sum
import kotlin.test.Test
import kotlin.test.assertEquals

class SumOperationTest {
  @Test
  fun `should sum correctly`() {
    val result = sum(16.0, 2.0, 2.0)
    assertEquals(20.0, result)
  }
}
