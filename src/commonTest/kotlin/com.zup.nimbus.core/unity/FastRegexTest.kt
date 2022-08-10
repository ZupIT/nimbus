package com.zup.nimbus.core.unity

import com.zup.nimbus.core.FastRegex
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FastRegexTest {
  @Test
  fun shouldFind() {
    val regex = FastRegex("""(^\w+)|(\.\w+)|(\[\d+\])""")
    val string = "abc[0].def.ghi[0][1][2].jkl"
    val matched = regex.find(string)
    assertEquals("abc", matched)
  }

  @Test
  fun shouldFindWithGroups() {
    val regex = FastRegex("""^"(\w+)": "([^"]*)"${'$'}""")
    val string = """"text": "Hello World""""
    val groups = regex.findWithGroups(string)
    assertEquals(3, groups?.size)
    assertEquals(string, groups?.get(0))
    assertEquals("text", groups?.get(1))
    assertEquals("Hello World", groups?.get(2))
  }

  @Test
  fun shouldFindAll() {
    val regex = FastRegex("""(^\w+)|(\.\w+)|(\[\d+\])""")
    val string = "abc[0].def.ghi[0][1][2].jkl"
    val matched = regex.findAll(string)
    assertEquals(8, matched.size)
    assertEquals("abc", matched[0])
    assertEquals("[0]", matched[1])
    assertEquals(".def", matched[2])
    assertEquals(".ghi", matched[3])
    assertEquals("[0]", matched[4])
    assertEquals("[1]", matched[5])
    assertEquals("[2]", matched[6])
    assertEquals(".jkl", matched[7])
  }

  @Test
  fun shouldFindAllWithGroups() {
    val regex = FastRegex("""@\{([\w\.\[\]]+)\}""")
    val string = "@{name.first} is @{age} years old/ His document number is @{documents[0].number}"
    val matched = regex.findAllWithGroups(string)

    assertEquals(3, matched.size)

    assertEquals(2, matched[0].size)
    assertEquals("@{name.first}", matched[0][0])
    assertEquals("name.first", matched[0][1])

    assertEquals(2, matched[1].size)
    assertEquals("@{age}", matched[1][0])
    assertEquals("age", matched[1][1])

    assertEquals(2, matched[2].size)
    assertEquals("@{documents[0].number}", matched[2][0])
    assertEquals("documents[0].number", matched[2][1])
  }

  @Test
  fun shouldMatch() {
    val regex = FastRegex("""(^\w+)|(\.\w+)|(\[\d+\])""")
    assertTrue(regex.matches("abc"))
    assertTrue(regex.matches(".test"))
    assertTrue(regex.matches("[5]"))
  }

  @Test
  fun shouldNotMatch() {
    val regex = FastRegex("""(^\w+)|(\.\w+)|(\[\d+\])""")
    val string = "..."
    assertFalse(regex.matches(string))
  }
}
