package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.getOtherOperations
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private val isEmpty = getOtherOperations()["isEmpty"]!!

class IsEmptyOperationTest {
  @Test
  fun `should return true when value is null`() {
    assertTrue { isEmpty(arrayOf(null)) as Boolean }
  }

  @Test
  fun `should verify if an Array is empty`() {
    assertTrue { isEmpty(arrayOf(listOf<Any>())) as Boolean }
  }

  @Test
  fun `should verify if an List is empty`() {
    assertTrue { isEmpty(arrayOf(listOf<Any>())) as Boolean }
  }

  @Test
  fun `should verify if an Map is empty`() {
    assertTrue { isEmpty(arrayOf(mapOf<String, Boolean>())) as Boolean }
  }

  @Test
  fun `should verify if an String is empty`() {
    assertTrue { isEmpty(arrayOf("")) as Boolean }
  }

  @Test
  fun `should return false even with an empty object`() {
    assertFalse { isEmpty(arrayOf(object { })) as Boolean }
    assertFalse { isEmpty(arrayOf(object { val hello = "world" })) as Boolean }
  }

  @Test
  fun `should return false with any other type that can not be empty`() {
    assertFalse { isEmpty(arrayOf(0)) as Boolean }
    assertFalse { isEmpty(arrayOf(432321.2333)) as Boolean }
    assertFalse { isEmpty(arrayOf(false)) as Boolean }
    assertFalse { isEmpty(arrayOf(true)) as Boolean }
  }
}
