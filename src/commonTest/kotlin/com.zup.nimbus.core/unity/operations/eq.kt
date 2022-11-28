package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val eq = coreUILibrary.getOperation("eq")!!

class EqOperationTest {
  @Test
  fun `should compare two objects and return true if they are equal`() {
    assertTrue { eq(listOf("a", "a")) as Boolean }
    assertTrue { eq(listOf(13.14, 13.14)) as Boolean }
    assertTrue { eq(listOf(15, 15.0)) as Boolean }
    assertTrue { eq(listOf(false, false)) as Boolean }
    assertTrue { eq(listOf(3, 3)) as Boolean }

    val obj = object { val hello = "world" }
    assertTrue { eq(listOf(obj, obj)) as Boolean }
  }

  @Test
  fun `should ignore data type when comparing - type coercion`() {
    // Given
    val operations = listOf<Pair<Any, Any>>(
      2 to 1,
      1 to 1,
      1 to 2,

      2.0 to 1.0,
      2.0 to 1,
      1.0 to 1,
      1.0 to 2,

      "2" to 1.0,
      "2" to 1,
      "2" to "1",

      "1" to "1",
      "1" to "1.0",
      "1.0" to 2.0,
      "1.0" to "2.0",

      "true" to "true",
      "true" to "false",
      "no" to "no",
      "no" to "yes",
      "true" to 2,
    )

    // When
    val result = operations.map {
      eq(listOf(it.first, it.second)) as Boolean
    }

    // Then
    val expected = listOf(
      false,
      true,
      false,

      false,
      false,
      true,
      false,

      false,
      false,
      false,
      true,
      true,
      false,
      false,

      true,
      false,
      true,
      false,
      false
    )

    assertEquals(expected, result)
  }
}
