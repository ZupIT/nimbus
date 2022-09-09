package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private val isEmpty = coreUILibrary.getOperation("isEmpty")!!

class IsEmptyOperationTest {
  @Test
  fun `should return true when value is null`() {
    assertTrue { isEmpty(listOf(null)) as Boolean }
  }

  @Test
  fun `should verify if an Array is empty`() {
    assertTrue { isEmpty(listOf(listOf<Any>())) as Boolean }
  }

  @Test
  fun `should verify if an List is empty`() {
    assertTrue { isEmpty(listOf(listOf<Any>())) as Boolean }
  }

  @Test
  fun `should verify if an Map is empty`() {
    assertTrue { isEmpty(listOf(mapOf<String, Boolean>())) as Boolean }
  }

  @Test
  fun `should verify if an String is empty`() {
    assertTrue { isEmpty(listOf("")) as Boolean }
  }

  @Test
  fun `should return false even with an empty object`() {
    assertFalse { isEmpty(listOf(object { })) as Boolean }
    assertFalse { isEmpty(listOf(object { val hello = "world" })) as Boolean }
  }

  @Test
  fun `should return false with any other type that can not be empty`() {
    assertFalse { isEmpty(listOf(0)) as Boolean }
    assertFalse { isEmpty(listOf(432321.2333)) as Boolean }
    assertFalse { isEmpty(listOf(false)) as Boolean }
    assertFalse { isEmpty(listOf(true)) as Boolean }
  }
}
