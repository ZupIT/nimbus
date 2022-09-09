package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertEquals

private val capitalize = coreUILibrary.getOperation("capitalize")!!

class CapitalizeOperationTest {
  @Test
  fun `should capitalize the first letter of a word`() {
    val result = capitalize(listOf("test"))
    assertEquals("Test", result)
  }

  @Test
  fun `should capitalize the first letter of the first word of a string`() {
    val result = capitalize(listOf("test the capitalize function"))
    assertEquals("Test the capitalize function", result)
  }
}
