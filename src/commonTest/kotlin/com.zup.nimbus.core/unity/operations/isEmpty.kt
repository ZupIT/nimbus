package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.isEmpty
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IsEmptyOperationTest {
  @Test
  fun `should return true when value is null`() {
    assertTrue { isEmpty(null) }
  }

  @Test
  fun `should verify if an Array is empty`() {
    assertTrue { isEmpty(arrayOf<String>()) }
  }

  @Test
  fun `should verify if an List is empty`() {
    assertTrue { isEmpty(listOf<Number>()) }
  }

  @Test
  fun `should verify if an Map is empty`() {
    assertTrue { isEmpty(mapOf<String, Boolean>()) }
  }

  @Test
  fun `should verify if an String is empty`() {
    assertTrue { isEmpty("") }
  }

  @Test
  fun `should return false even with an empty object`() {
    assertFalse { isEmpty(object { }) }
    assertFalse { isEmpty(object { val hello = "world" }) }
  }

  @Test
  fun `should return false with any other type that can not be empty`() {
    assertFalse { isEmpty(0) }
    assertFalse { isEmpty(432321.2333) }
    assertFalse { isEmpty(false) }
    assertFalse { isEmpty(true) }
  }
}
