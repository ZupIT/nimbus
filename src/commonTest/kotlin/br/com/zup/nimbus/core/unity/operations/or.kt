package br.com.zup.nimbus.core.unity.operations

import br.com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private val or = coreUILibrary.getOperation("or")!!

class OrOperationTest {
  @Test
  fun `should return true when at least one condition is true`() {
    val a = 1
    val b = 2
    assertTrue { or(listOf(a > b, a == b, a < b)) as Boolean }
    assertTrue { or(listOf(a < b)) as Boolean }
    assertFalse { or(listOf(a == b)) as Boolean }
    assertFalse { or(listOf(a > b, a == b)) as Boolean }
  }
}
