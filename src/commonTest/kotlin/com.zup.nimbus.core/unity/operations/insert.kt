package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.insert
import kotlin.test.Test
import kotlin.test.assertEquals

class InsertOperationTest {
  @Test
  fun `should insert at last position when no index is provided`() {
    var list = mutableListOf<Any>("one", "two", "three")
    list = insert(arrayOf(list.toTypedArray(), "four")) as MutableList<Any>

    assertEquals(list.size, 4)
    assertEquals(list[3], "four")
  }

  @Test
  fun `should insert at a indexed position`() {
    var list = mutableListOf<Any>("one", "two", "three")
    list = insert(arrayOf(list.toTypedArray(), "four", 1)) as MutableList<Any>

    assertEquals(list.size, 4)
    assertEquals(list[0], "one")
    assertEquals(list[1], "four")
    assertEquals(list[2], "two")
    assertEquals(list[3], "three")
  }
}
