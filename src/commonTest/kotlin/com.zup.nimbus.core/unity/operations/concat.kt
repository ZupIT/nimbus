package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.concat
import kotlin.test.Test
import kotlin.test.assertEquals

class ConcatOperationTest {
  @Test
  fun `should concat all the string into a single one`() {
    val result = concat("one", "-two-", "three")
    assertEquals("one-two-three", result)
  }
}
