package br.com.zup.nimbus.core.unity.operations

import br.com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertEquals

private val uppercase = coreUILibrary.getOperation("uppercase")!!

class UppercaseOperationTest {
  @Test
  fun `should change all the lower letters to capitalized ones`() {
    assertEquals("THIS IS A TEST", uppercase(listOf("This is a Test")))
  }
}
