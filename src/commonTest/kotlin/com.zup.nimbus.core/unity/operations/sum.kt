package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertEquals

private val sum = coreUILibrary.getOperation("sum")!!

class SumOperationTest {
  @Test
  fun `should sum correctly integers`() {
    val result = sum(listOf(16, 2, 2))
    assertEquals(20, result)
  }

  @Test
  fun `should sum correctly doubles`() {
    val result = sum(listOf(16.3, 2.4, 2.9))
    assertEquals(21.599999999999998, result)
  }

  @Test
  fun `should sum correctly mixed`() {
    val result = sum(listOf(16, 2.5, 2))
    assertEquals(20.5, result)
  }
}
