package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.lowercase
import kotlin.test.Test
import kotlin.test.assertEquals

class LowercaseOperationTest {
  @Test
  fun `should change all the capitalized letter to lower cased ones`() {
    assertEquals("this is a test", lowercase("This is a TEST"))
  }
}
