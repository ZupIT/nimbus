package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private val isNull = coreUILibrary.getOperation("isNull")!!

class IsNullOperationTest {
  @Test
  fun `should return true when the parameter is null`() {
    assertTrue { isNull(listOf(null)) as Boolean }
  }

  @Test
  fun `should return false when the parameter is not null`() {
    assertFalse { isNull(listOf("")) as Boolean }
  }
}
