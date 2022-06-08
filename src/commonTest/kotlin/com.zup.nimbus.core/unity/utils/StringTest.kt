package com.zup.nimbus.core.unity.utils

import com.zup.nimbus.core.utils.addPrefix
import com.zup.nimbus.core.utils.capitalizeFirstLetter
import com.zup.nimbus.core.utils.removePrefix
import com.zup.nimbus.core.utils.removeSuffix
import kotlin.test.Test
import kotlin.test.assertEquals

class StringTest {
  @Test
  fun `should remove the prefix of a string`() {
    val original = "This is my Test"
    val result = removePrefix(original, "This is my ")
    assertEquals("Test", result)
  }

  @Test
  fun `should add a prefix in a string`() {
    val original = "Test"
    val result = addPrefix(original, "This is my ")
    assertEquals("This is my Test", result)
  }

  @Test
  fun `should not add a prefix in a string when the prefix is equal to the first char`() {
    val original = "Test"
    val result = addPrefix(original, "T")
    assertEquals("Test", result)
  }

  @Test
  fun `should remove the suffix of a string`() {
    val original = "Test content"
    val result = removeSuffix(original, " content")
    assertEquals("Test", result)
  }

  @Test
  fun `should capitalize the first letter of a string`() {
    val original = "this is my test"
    val result = capitalizeFirstLetter(original)
    assertEquals("This is my test", result)
  }
}
