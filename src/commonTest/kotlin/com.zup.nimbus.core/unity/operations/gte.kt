package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private val gte = coreUILibrary.getOperation("gte")!!

class GteOperationTest {
  @Test
  fun `should compare two numbers and tell if a is greater or is equal b`() {
    assertTrue { gte(listOf(5, 4)) as Boolean }
    assertTrue { gte(listOf(5.4567, 5.4566)) as Boolean }
    assertTrue { gte(listOf(5, 5)) as Boolean }
    assertTrue { gte(listOf(5.4567, 5.4567)) as Boolean }
    assertFalse { gte(listOf(5, 5.4566)) as Boolean }
    assertFalse { gte(listOf(5.4565, 5.4566)) as Boolean }
  }
}
