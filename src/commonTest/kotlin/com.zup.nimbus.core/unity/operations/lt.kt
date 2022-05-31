package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.lt
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LtOperationTest {
  @Test
  fun `should compare two numbers and tell that a is lesser than b`() {
    assertTrue { lt(4.0, 5.0) }
    assertTrue { lt(5.4566, 5.4567) }
    assertFalse { lt(5.4566, 5.0) }
  }
}
