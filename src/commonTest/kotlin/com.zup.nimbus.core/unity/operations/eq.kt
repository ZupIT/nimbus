package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertTrue

private val eq = coreUILibrary.getOperation("eq")!!

class EqOperationTest {
  @Test
  fun `should compare two objects and return true if they are equal`() {
    assertTrue { eq(listOf("a", "a")) as Boolean }
    assertTrue { eq(listOf(13.14, 13.14)) as Boolean }
    assertTrue { eq(listOf(15, 15.0)) as Boolean }
    assertTrue { eq(listOf(false, false)) as Boolean }
    assertTrue { eq(listOf(3, 3)) as Boolean }

    val obj = object { val hello = "world" }
    assertTrue { eq(listOf(obj, obj)) as Boolean }
  }
}
