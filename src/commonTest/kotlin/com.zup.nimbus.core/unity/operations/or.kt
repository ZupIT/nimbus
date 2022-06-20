package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.or
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OrOperationTest {
  @Test
  fun `should return true when at least one condition is true`() {
    val a = 1
    val b = 2
    assertTrue { or(arrayOf(a > b, a == b, a < b)) as Boolean }
    assertTrue { or(arrayOf(a < b)) as Boolean }
    assertFalse { or(arrayOf(a == b)) as Boolean }
    assertFalse { or(arrayOf(a > b, a == b)) as Boolean }
  }
}