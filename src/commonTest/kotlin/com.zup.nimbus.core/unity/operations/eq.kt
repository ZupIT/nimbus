package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.getOtherOperations
import kotlin.test.Test
import kotlin.test.assertTrue

private val eq = getOtherOperations()["eq"]!!

class EqOperationTest {
  @Test
  fun `should compare two objects and return true if they are equal`() {
    assertTrue { eq(arrayOf("a", "a")) as Boolean }
    assertTrue { eq(arrayOf(13.14, 13.14)) as Boolean }
    assertTrue { eq(arrayOf(15, 15.0)) as Boolean }
    assertTrue { eq(arrayOf(false, false)) as Boolean }
    assertTrue { eq(arrayOf(3, 3)) as Boolean }

    val obj = object { val hello = "world" }
    assertTrue { eq(arrayOf(obj, obj)) as Boolean }
  }
}
