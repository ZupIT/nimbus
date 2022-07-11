package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.getStringOperations
import kotlin.test.Test
import kotlin.test.assertEquals

private val uppercase = getStringOperations()["uppercase"]!!

class UppercaseOperationTest {
  @Test
  fun `should change all the lower letters to capitalized ones`() {
    assertEquals("THIS IS A TEST", uppercase(arrayOf("This is a Test")))
  }
}
