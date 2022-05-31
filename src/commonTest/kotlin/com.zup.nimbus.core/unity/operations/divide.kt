package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.divide
import kotlin.test.Test
import kotlin.test.assertEquals

class DivideOperationTest {
  @Test
  fun `should divide correctly`() {
    val result = divide(16.0, 2.0, 2.0)
    assertEquals(4.0, result)
  }
}
