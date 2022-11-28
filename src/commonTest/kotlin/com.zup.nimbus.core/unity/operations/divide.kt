package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertEquals

private val divide = coreUILibrary.getOperation("divide")!!

class DivideOperationTest {
  @Test
  fun `should divide correctly integers`() {
    val result = divide(listOf(16, 2, 2))
    assertEquals(4, result)
  }

  @Test
  fun `should divide correctly doubles`() {
    val result = divide(listOf(16.3, 2.4, 2.9))
    assertEquals(2.341954022988506, result)
  }

  @Test
  fun `should divide correctly mixed`() {
    val result = divide(listOf(16, 2.5, 2))
    assertEquals(3.2, result)
  }

  @Test
  fun `should divide despite the type of the data - type coercion`() {
    // Given
    val operations = listOf<Pair<Any, Any>>(
      6 to 4, 4.5 to 6, 4.5 to 4.5, 6 to 3.0,
      3 to 1.5, 2.0 to 1, "1" to 1.0, 2.5 to "1.0", "1" to "1", "2" to 1,
      1 to true, "1" to false, "" to ""
    )

    // When
    val result = operations.map {
      divide(listOf(it.first, it.second))
    }

    // Then
    val expected = listOf<Number?>(1.5, 0.75, 1.0, 2.0, 2.0, 2.0, 1.0, 2.5, 1, 2, null, null, null)
    assertEquals(expected, result)
  }
}
