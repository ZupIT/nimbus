package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private val lt = coreUILibrary.getOperation("lt")!!

class LtOperationTest {
  @Test
  fun `should compare two numbers and tell that a is lesser than b`() {
    assertTrue { lt(listOf(4, 5)) as Boolean }
    assertTrue { lt(listOf(5.4566, 5.4567)) as Boolean }
    assertFalse { lt(listOf(5.4566, 5)) as Boolean }
  }
}
