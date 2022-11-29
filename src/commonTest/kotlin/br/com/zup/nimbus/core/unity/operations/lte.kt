package br.com.zup.nimbus.core.unity.operations

import br.com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private val lte = coreUILibrary.getOperation("lte")!!

class LteOperationTest {
  @Test
  fun `should compare two numbers and tell if a is lesser than b`() {
    assertTrue { lte(listOf(4, 5)) as Boolean }
    assertTrue { lte(listOf(5.4566, 5.4567)) as Boolean }
    assertTrue { lte(listOf(5, 5)) as Boolean }
    assertTrue { lte(listOf(5.4567, 5.4567)) as Boolean }
    assertFalse { lte(listOf(5.4566, 5)) as Boolean }
    assertFalse { lte(listOf(5.4566, 5.4565)) as Boolean }
  }

  @Test
  fun `should ignore data type when comparing - type coercion`() {
    //Given
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
      "true" to 2,
    )

    //When
    val result = operations.map {
      lte(listOf(it.first, it.second)) as Boolean
    }

    //Then
    val expected = listOf(
      false,
      true,
      true,
      false,
      false,
      true,
      true,
      false,
      false,
      false,
      true,
      true,
      true,
      true,
      false
    )

    assertEquals(expected, result)
  }
}
