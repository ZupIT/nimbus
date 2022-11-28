package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private val gt = coreUILibrary.getOperation("gt")!!

class GtOperationTest {
  @Test
  fun `should compare two numbers and tell if a is greater than b`() {
    assertTrue { gt(listOf(5, 4)) as Boolean }
    assertTrue { gt(listOf(5.4567, 5.4566)) as Boolean }
    assertFalse { gt(listOf(5, 5.4566)) as Boolean }
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
      "2" to 1.0,
      "2" to 1,
      "2" to "1",
      "1" to "1",
      "1" to "1.0",
      "1.0" to 2.0,
      "1.0" to "2.0",
      "true" to 2,
    )

    // When
    val result = operations.map {
      gt(listOf(it.first, it.second)) as Boolean
    }

    // Then
    val expected = listOf(
      true,
      false,
      false,
      true,
      true,
      false,
      true,
      true,
      true,
      false,
      false,
      false,
      false,
      false
    )

    assertEquals(expected, result)
  }
}
