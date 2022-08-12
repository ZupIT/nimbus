package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.getNumberOperations
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private val lte = getNumberOperations()["lte"]!!

class LteOperationTest {
  @Test
  fun `should compare two numbers and tell if a is lesser than b`() {
    assertTrue { lte(listOf(4, 5)) as Boolean }
    assertTrue { lte(listOf(5.4566, 5.4567)) as Boolean }
    assertTrue { lte(listOf(5, 5)) as Boolean }
    assertTrue { lte(listOf(5.4567, 5.4567)) as Boolean }
    assertFalse { lte(listOf(5.4566, 5)) as Boolean }
    assertFalse { lte(listOf(5.4566, 5.4565)) as Boolean }
  }
}
