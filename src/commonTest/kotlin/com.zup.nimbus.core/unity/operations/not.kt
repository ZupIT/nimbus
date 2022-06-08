package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.not
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NotOperationTest {
  @Test
  fun `should return true when a negative condition is passed`() {
    val a = 1
    val b = 2
    assertTrue { not(arrayOf(a > b)) as Boolean }
    assertFalse { not(arrayOf(a < b)) as Boolean }
  }
}
