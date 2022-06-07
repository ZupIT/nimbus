package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.and
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AndOperationTest {
  private val x = 2
  private val y = 1

  @Test
  fun `should return true when all args are true`() {
    val result = and(arrayOf(x > y, (y + 2) > x, (y + 2) < (x * 3))) as Boolean
    assertTrue { result }
  }

  @Test
  fun `should return false when one argument is not true`() {
    val result = and(arrayOf(x > y, (y + 2) < x, (y + 2) < (x * 3))) as Boolean
    assertFalse { result }
  }

  @Test
  fun `should return false when all arguments are false`() {
    val result = and(arrayOf(x < y, (y + 2) < x, (y + 2) > (x * 3))) as Boolean
    assertFalse { result }
  }
}
