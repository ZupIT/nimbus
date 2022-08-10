package com.zup.nimbus.core.unity

import com.zup.nimbus.core.regex.FastRegex
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
    assertEquals(3, groups?.values?.size)
    assertEquals(string, groups?.values?.get(0))
    assertEquals("text", groups?.values?.get(1))
    assertEquals("Hello World", groups?.values?.get(2))
  }

  @Test
  fun shouldDestructureGroup() {
    val regex = FastRegex("""(\d)-(\d)-(\d)-(\d)-(\d)-(\d)-(\d)-(\d)-(\d)""")
    val string = "1-2-3-4-5-6-7-8-9"
    val (first, second, third, fourth, fifth, sixth, seventh, eighth, ninth) = regex.findWithGroups(string)!!.destructured
    assertEquals("1", first)
    assertEquals("2", second)
    assertEquals("3", third)
    assertEquals("4", fourth)
    assertEquals("5", fifth)
    assertEquals("6", sixth)
    assertEquals("7", seventh)
    assertEquals("8", eighth)
    assertEquals("9", ninth)
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

    assertEquals(2, matched[0].values.size)
    assertEquals("@{name.first}", matched[0].values[0])
    assertEquals("name.first", matched[0].values[1])

    assertEquals(2, matched[1].values.size)
    assertEquals("@{age}", matched[1].values[0])
    assertEquals("age", matched[1].values[1])

    assertEquals(2, matched[2].values.size)
    assertEquals("@{documents[0].number}", matched[2].values[0])
    assertEquals("documents[0].number", matched[2].values[1])
  }

  @Test
  fun shouldReplace() {
    val regex = FastRegex(""",|\.""")
    val replaced = regex.replace("Abc, def. Ghijk, lmn, opqr: st. Uvw, x, yz.", "-+")
    assertEquals("Abc-+ def-+ Ghijk-+ lmn-+ opqr: st-+ Uvw-+ x-+ yz-+", replaced)
  }

  @Test
  fun shouldReplaceWithIndexedExpressions() {
    val regex = FastRegex(""""([^"]+)":\s*"([^"]+)"""")
    val input = """{ "name": "John", "lastName":"Stone", "age":   "30" }"""
    val replaceExpression = "Property $1 is $2"
    val replaced = regex.replace(input, replaceExpression)
    assertEquals("{ Property name is John, Property lastName is Stone, Property age is 30 }", replaced)
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
    assertFalse(regex.matches("abc[0]"))
  }

  @Test
  fun shouldContainMatch() {
    val regex = FastRegex("""(^\w+)|(\.\w+)|(\[\d+\])""")
    assertTrue(regex.containsMatchIn("abc[0]"))
  }

  @Test
  fun shouldNotContainMatch() {
    val regex = FastRegex("""(^\w+)|(\.\w+)|(\[\d+\])""")
    assertFalse(regex.containsMatchIn("..."))
  }
}
