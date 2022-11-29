package br.com.zup.nimbus.core.unity.operations

import br.com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertEquals

private val sum = coreUILibrary.getOperation("sum")!!

class SumOperationTest {
  @Test
  fun `should sum correctly integers`() {
    val result = sum(listOf(16, 2, 2))
    assertEquals(20, result)
  }

  @Test
  fun `should sum correctly doubles`() {
    val result = sum(listOf(16.3, 2.4, 2.9))
    assertEquals(21.599999999999998, result)
  }

  @Test
  fun `should sum correctly mixed`() {
    val result = sum(listOf(16, 2.5, 2))
    assertEquals(20.5, result)
  }

  @Test
  fun `should sum despite the type of the data - type coercion`() {
    // Given
    val operations = listOf<Pair<Any, Any>>(
      6 to 4, 4.5 to 6, 4.5 to 4.5, 6 to 4.5,
      1 to 1.5, 2.0 to 1, "1" to 1.0, 2.5 to "1.0", "1" to "1", "2" to 1,
      1 to true, "1" to false, "" to ""
    )

    // When
    val result = operations.map {
      sum(listOf(it.first, it.second))
    }

    // Then
    val expected = listOf<Number?>(10, 10.5, 9.0, 10.5, 2.5, 3.0, 2.0, 3.5, 2, 3, null, null, null)
    assertEquals(expected, result)
  }
}
