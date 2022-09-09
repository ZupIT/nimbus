package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertEquals

private val replace = coreUILibrary.getOperation("replace")!!

class ReplaceOperationTest {
  @Test
  fun `should return the range of characters between two indexes`() {
    val result = replace(listOf("This is a Test text.", "This is a ", "Replaced "))
    assertEquals("Replaced Test text.", result)
  }
}
