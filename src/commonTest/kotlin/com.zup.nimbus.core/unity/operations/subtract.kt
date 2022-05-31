package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.subtract
import kotlin.test.Test
import kotlin.test.assertEquals

class SubtractOperationTest {
  @Test
  fun `should subtract correctly`() {
    val result = subtract(16.0, 2.0, 2.0)
    assertEquals(12.0, result)
  }
}
