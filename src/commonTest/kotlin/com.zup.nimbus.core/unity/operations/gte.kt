package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.gte
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GteOperationTest {
  @Test
  fun `should compare two numbers and tell if a is greater or is equal b`() {
    assertTrue { gte(arrayOf(5.0, 4.0)) as Boolean }
    assertTrue { gte(arrayOf(5.4567, 5.4566)) as Boolean }
    assertTrue { gte(arrayOf(5.0, 5.0)) as Boolean }
    assertTrue { gte(arrayOf(5.4567, 5.4567)) as Boolean }
    assertFalse { gte(arrayOf(5.0, 5.4566)) as Boolean }
    assertFalse { gte(arrayOf(5.4565, 5.4566)) as Boolean }
  }
}
