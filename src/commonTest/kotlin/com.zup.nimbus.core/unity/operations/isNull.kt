package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.isNull
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IsNullOperationTest {
  @Test
  fun `should return true when the parameter is null`() {
    assertTrue { isNull(arrayOf(null)) as Boolean }
  }

  @Test
  fun `should return false when the parameter is not null`() {
    assertFalse { isNull(arrayOf("")) as Boolean }
  }
}
