package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.getNumberOperations
import kotlin.test.Test
import kotlin.test.assertEquals

private val divide = getNumberOperations()["divide"]!!

class DivideOperationTest {
  @Test
  fun `should divide correctly integers`() {
    val result = divide(arrayOf(16, 2, 2))
    assertEquals(4, result)
  }

  @Test
  fun `should divide correctly doubles`() {
    val result = divide(arrayOf(16.3, 2.4, 2.9))
    assertEquals(2.341954022988506, result)
  }

  @Test
  fun `should divide correctly mixed`() {
    val result = divide(arrayOf(16, 2.5, 2))
    assertEquals(3.2, result)
  }
}
