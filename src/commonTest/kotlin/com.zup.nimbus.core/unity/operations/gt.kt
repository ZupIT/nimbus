package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.gt
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GtOperationTest {
  @Test
  fun `should compare two numbers and tell if a is greater than b`() {
    assertTrue { gt(arrayOf(5, 4)) as Boolean }
    assertTrue { gt(arrayOf(5.4567, 5.4566)) as Boolean }
    assertFalse { gt(arrayOf(5, 5.4566)) as Boolean }
  }
}
