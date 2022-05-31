package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.eq
import kotlin.test.Test
import kotlin.test.assertTrue

class EqOperationTest {
  @Test
  fun `should compare two objects and return true if they are equal`() {
    assertTrue { eq("a", "a") }
    assertTrue { eq(13.14, 13.14) }
    assertTrue { eq(false, false) }
    assertTrue { eq(3, 3) }

    val obj = object { val hello = "world" }
    assertTrue { eq(obj, obj) }
  }
}
