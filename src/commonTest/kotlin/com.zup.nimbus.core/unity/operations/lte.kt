package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.lte
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LteOperationTest {
  @Test
  fun `should compare two numbers and tell if a is lesser than b`() {
    assertTrue { lte(arrayOf(4.0, 5.0)) as Boolean }
    assertTrue { lte(arrayOf(5.4566, 5.4567)) as Boolean }
    assertTrue { lte(arrayOf(5.0, 5.0)) as Boolean }
    assertTrue { lte(arrayOf(5.4567, 5.4567)) as Boolean }
    assertFalse { lte(arrayOf(5.4566, 5.0)) as Boolean }
    assertFalse { lte(arrayOf(5.4566, 5.4565)) as Boolean }
  }
}
