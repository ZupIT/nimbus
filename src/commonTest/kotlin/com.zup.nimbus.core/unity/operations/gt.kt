package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.getNumberOperations
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private val gt = getNumberOperations()["gt"]!!

class GtOperationTest {
  @Test
  fun `should compare two numbers and tell if a is greater than b`() {
    assertTrue { gt(listOf(5, 4)) as Boolean }
    assertTrue { gt(listOf(5.4567, 5.4566)) as Boolean }
    assertFalse { gt(listOf(5, 5.4566)) as Boolean }
  }
}
