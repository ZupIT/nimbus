package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.insert
import kotlin.test.Test
import kotlin.test.assertEquals

class InsertOperationTest {
  @Test
  fun `should insert at last position when no index is provided`() {
    var list = mutableListOf("one", "two", "three")
    list = insert(list, "four")

    assertEquals(list.size, 4)
    assertEquals(list[3], "four")
  }

  @Test
  fun `should insert at a indexed position`() {
    var list = mutableListOf("one", "two", "three")
    list = insert(list, "four", 1)

    assertEquals(list.size, 4)
    assertEquals(list[0], "one")
    assertEquals(list[1], "four")
    assertEquals(list[2], "two")
    assertEquals(list[3], "three")
  }
}
