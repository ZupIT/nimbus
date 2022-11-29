package br.com.zup.nimbus.core.unity.operations

import br.com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private val not = coreUILibrary.getOperation("not")!!

class NotOperationTest {
  @Test
  fun `should return true when a negative condition is passed`() {
    val a = 1
    val b = 2
    assertTrue { not(listOf(a > b)) as Boolean }
    assertFalse { not(listOf(a < b)) as Boolean }
  }
}
