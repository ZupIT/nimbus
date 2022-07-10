package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.getStringOperations
import kotlin.test.Test
import kotlin.test.assertTrue

private val match = getStringOperations()["match"]!!

class MatchOperationTest {
  @Test
  fun `should return true when the text fully matches the regex expression`() {
    val regex = """^.*\{.*}?.*$"""
    val result = match(arrayOf("This is a {Test} inside a string text", regex)) as Boolean
    assertTrue { result }
  }
}
