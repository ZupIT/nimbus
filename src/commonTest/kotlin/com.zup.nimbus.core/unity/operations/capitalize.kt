package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.capitalize
import kotlin.test.Test
import kotlin.test.assertEquals

class CapitalizeOperationTest {
  @Test
  fun `should capitalize the first letter of a word`() {
    val result = capitalize(arrayOf("test"))
    assertEquals("Test", result)
  }

  @Test
  fun `should capitalize the first letter of the first word of a string`() {
    val result = capitalize(arrayOf("test the capitalize function"))
    assertEquals("Test the capitalize function", result)
  }
}