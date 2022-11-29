package br.com.zup.nimbus.core.unity.operations

import br.com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertEquals

private val substr = coreUILibrary.getOperation("substr")!!

class SubstrOperationTest {
  @Test
  fun `should return the range of characters between two indexes`() {
    assertEquals("Test", substr(listOf("This is a Test text.", 10, 14)))
  }
}
