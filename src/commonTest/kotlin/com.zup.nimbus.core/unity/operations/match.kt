package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.match
import kotlin.test.Test
import kotlin.test.assertTrue

class MatchOperationTest {
  @Test
  fun `should return true when the text fully matches the regex expression`() {
    val regex = """^.*\{.*}?.*$""".toRegex()
    val result = match("This is a {Test} inside a string text", regex)
    assertTrue { result }
  }
}
