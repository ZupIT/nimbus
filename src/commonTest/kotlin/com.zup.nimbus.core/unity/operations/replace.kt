package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.getStringOperations
import kotlin.test.Test
import kotlin.test.assertEquals

private val replace = getStringOperations()["replace"]!!

class ReplaceOperationTest {
  @Test
  fun `should return the range of characters between two indexes`() {
    val result = replace(arrayOf("This is a Test text.", "This is a ", "Replaced "))
    assertEquals("Replaced Test text.", result)
  }
}
