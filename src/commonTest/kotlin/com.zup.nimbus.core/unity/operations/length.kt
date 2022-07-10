package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.getOtherOperations
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val length = getOtherOperations()["length"]!!

class LengthOperationTest {
  @Test
  fun `should return the correct length of a list`() {
    var list = listOf(1,2,3,4,5)
    var result = length(arrayOf(list))
    assertEquals(5, result)

    list = emptyList()
    result = length(arrayOf(list))
    assertEquals(0, result)

    list = mutableListOf(1,2,3,4,5)
    result = length(arrayOf(list))
    assertEquals(5, result)
  }

  @Test
  fun `should return the correct length of a map`() {
    var map = mapOf(
      "one" to 1,
      "two" to 2,
      "three" to 3,
      "four" to 4,
      "five" to 5
    )
    var result = length(arrayOf(map))
    assertEquals(5, result)

    map = emptyMap<String, Int>()
    result = length(arrayOf(map))
    assertEquals(0, result)

    map = mutableMapOf(
      "one" to 1,
      "two" to 2,
      "three" to 3,
      "four" to 4,
      "five" to 5
    )
    result = length(arrayOf(map))
    assertEquals(5, result)
  }

  @Test
  fun `should return the correct length of a string`() {
    var string = "Test"
    var result = length(arrayOf(string))
    assertEquals(4, result)

    string = "This is a test"
    result = length(arrayOf(string))
    assertEquals(14, result)

    string = ""
    result = length(arrayOf(string))
    assertEquals(0, result)
  }
}
