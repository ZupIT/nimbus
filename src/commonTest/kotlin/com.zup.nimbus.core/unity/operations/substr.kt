package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.getStringOperations
import kotlin.test.Test
import kotlin.test.assertEquals

private val substr = getStringOperations()["substr"]!!

class SubstrOperationTest {
  @Test
  fun `should return the range of characters between two indexes`() {
    assertEquals("Test", substr(arrayOf("This is a Test text.", 10, 14)))
  }
}
