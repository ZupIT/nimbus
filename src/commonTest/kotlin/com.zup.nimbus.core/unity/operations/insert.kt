package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertEquals

private val insert = coreUILibrary.getOperation("insert")!!

class InsertOperationTest {
  @Test
  fun `should insert at last position when no index is provided`() {
    var list = listOf<Any>("one", "two", "three")
    list = insert(listOf(list, "four")) as List<Any>

    assertEquals(list.size, 4)
    assertEquals(list[3], "four")
  }

  @Test
  fun `should insert at a indexed position`() {
    var list = listOf<Any>("one", "two", "three")
    list = insert(listOf(list, "four", 1)) as List<Any>

    assertEquals(list.size, 4)
    assertEquals(list[0], "one")
    assertEquals(list[1], "four")
    assertEquals(list[2], "two")
    assertEquals(list[3], "three")
  }
}
