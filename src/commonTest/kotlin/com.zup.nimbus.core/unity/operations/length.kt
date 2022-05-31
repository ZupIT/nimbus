package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.length
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LengthOperationTest {
  @Test
  fun `should return the correct length of an array`() {
    var array = arrayOf(1,2,3,4,5)
    var result = length(array)
    assertEquals(5, result)

    array = emptyArray()
    result = length(array)
    assertEquals(0, result)
  }

  @Test
  fun `should return the correct length of a list`() {
    var list = listOf(1,2,3,4,5)
    var result = length(list)
    assertEquals(5, result)

    list = emptyList()
    result = length(list)
    assertEquals(0, result)

    list = mutableListOf(1,2,3,4,5)
    result = length(list)
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
    var result = length(map)
    assertEquals(5, result)

    map = emptyMap<String, Int>()
    result = length(map)
    assertEquals(0, result)

    map = mutableMapOf(
      "one" to 1,
      "two" to 2,
      "three" to 3,
      "four" to 4,
      "five" to 5
    )
    result = length(map)
    assertEquals(5, result)
  }

  @Test
  fun `should return the correct length of a string`() {
    var string = "Test"
    var result = length(string)
    assertEquals(4, result)

    string = "This is a test"
    result = length(string)
    assertEquals(14, result)

    string = ""
    result = length(string)
    assertEquals(0, result)
  }

  @Test
  fun `should return the length of the object passed when called the toString method`() {
    assertEquals(4, length(1234))
    assertEquals(1, length(0))
    assertEquals(7, length(13.1213))
    assertEquals(4, length(true))
    assertEquals(5, length(false))
    assertTrue { length(object { val hello = "world" }) > 0 }
  }
}
