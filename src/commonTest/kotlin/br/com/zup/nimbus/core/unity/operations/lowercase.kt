package br.com.zup.nimbus.core.unity.operations

import br.com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertEquals

private val lowercase = coreUILibrary.getOperation("lowercase")!!

class LowercaseOperationTest {
  @Test
  fun `should change all the capitalized letter to lower cased ones`() {
    assertEquals("this is a test", lowercase(listOf("This is a TEST")))
  }
}
