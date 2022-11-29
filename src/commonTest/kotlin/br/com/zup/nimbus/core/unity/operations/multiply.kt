package br.com.zup.nimbus.core.unity.operations

import br.com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertEquals

private val multiply = coreUILibrary.getOperation("multiply")!!

class MultiplyOperationTest {
  @Test
  fun `should multiply correctly integers`() {
    val result = multiply(listOf(16, 2, 2))
    assertEquals(64, result)
  }

  @Test
  fun `should multiply correctly doubles`() {
    val result = multiply(listOf(16.3, 2.4, 2.9))
    assertEquals(113.448, result)
  }

  @Test
  fun `should multiply correctly mixed`() {
    val result = multiply(listOf(16, 2.5, 2))
    assertEquals(80.0, result)
  }

  @Test
  fun `should multiply despite the type of the data - type coercion`() {
    // Given
    val operations = listOf<Pair<Any, Any>>(
      6 to 4, 4.5 to 6, 4.5 to 4.5, 6 to 4.5,
      1 to 1.5, 2.0 to 1, "1" to 1.0, 2.5 to "1.0", "1" to "1", "2" to 1,
      1 to true, "1" to false, "" to ""
    )

    // When
    val result = operations.map {
      multiply(listOf(it.first, it.second))
    }

    // Then
    val expected = listOf<Number?>(24, 27.0, 20.25, 27.0, 1.5, 2.0, 1.0, 2.5, 1, 2, null, null, null)
    assertEquals(expected, result)
  }
}
