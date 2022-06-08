package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.length
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LengthOperationTest {
  @Test
  fun `should return the correct length of an array`() {
    var array = arrayOf(1,2,3,4,5)
    var result = length(arrayOf(array))
    assertEquals(5, result)

    array = emptyArray()
    result = length(arrayOf(array))
    assertEquals(0, result)
  }

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

  @Test
  fun `should return the length of the object passed when called the toString method`() {
    assertEquals(4, length(arrayOf(1234)))
    assertEquals(1, length(arrayOf(0)))
    assertEquals(7, length(arrayOf(13.1213)))
    assertEquals(4, length(arrayOf(true)))
    assertEquals(5, length(arrayOf(false)))
    assertTrue { length(arrayOf(object { val hello = "world" })) as Int > 0 }
  }
}
