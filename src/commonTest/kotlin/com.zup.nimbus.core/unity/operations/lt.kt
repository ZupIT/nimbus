package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.getNumberOperations
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private val lt = getNumberOperations()["lt"]!!

class LtOperationTest {
  @Test
  fun `should compare two numbers and tell that a is lesser than b`() {
    assertTrue { lt(arrayOf(4, 5)) as Boolean }
    assertTrue { lt(arrayOf(5.4566, 5.4567)) as Boolean }
    assertFalse { lt(arrayOf(5.4566, 5)) as Boolean }
  }
}
