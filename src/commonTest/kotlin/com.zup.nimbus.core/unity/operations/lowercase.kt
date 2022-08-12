package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.getStringOperations
import kotlin.test.Test
import kotlin.test.assertEquals

private val lowercase = getStringOperations()["lowercase"]!!

class LowercaseOperationTest {
  @Test
  fun `should change all the capitalized letter to lower cased ones`() {
    assertEquals("this is a test", lowercase(listOf("This is a TEST")))
  }
}
