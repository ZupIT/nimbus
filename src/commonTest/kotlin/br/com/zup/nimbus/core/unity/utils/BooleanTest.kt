package br.com.zup.nimbus.core.unity.utils

import br.com.zup.nimbus.core.utils.then
import kotlin.test.Test
import kotlin.test.assertEquals

class BooleanTest {
  private var mockValue = "Test"

  @Test
  fun `should return the first value when the condition is true`() {
    val result = ((mockValue == "Test") then 1) ?: 2
    assertEquals(1, result)
  }

  @Test
  fun `should return the second value when the condition is false`() {
    mockValue = "Tset"
    val result = ((mockValue == "Test") then 1) ?: 2
    assertEquals(2, result)
  }
}
