package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.uppercase
import kotlin.test.Test
import kotlin.test.assertEquals

class UppercaseOperationTest {
  @Test
  fun `should change all the lower letters to capitalized ones`() {
    assertEquals("THIS IS A TEST", uppercase(arrayOf("This is a Test")))
  }
}
