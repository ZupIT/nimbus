package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.gt
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GtOperationTest {
  @Test
  fun `should compare two numbers and tell if a is greater than b`() {
    assertTrue { gt(5.0, 4.0) }
    assertTrue { gt(5.4567, 5.4566) }
    assertFalse { gt(5.0, 5.4566) }
  }
}
