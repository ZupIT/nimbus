package com.zup.nimbus.core.unity.render

import com.zup.nimbus.core.log.DefaultLogger
import com.zup.nimbus.core.operations.defaultOperations
import com.zup.nimbus.core.render.containsExpression
import com.zup.nimbus.core.render.resolveExpressions
import com.zup.nimbus.core.tree.RenderNode
import com.zup.nimbus.core.tree.ServerDrivenState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExpressionTest {
  // #Tests for function "containsExpression"
  @Test
  fun `should find expression inside a text`() {
    val result = containsExpression("This is a text with an @{expression} inside of the text!")
    assertEquals(true, result)
  }

  @Test
  fun `should find expression when expression is whole text`() {
    val result = containsExpression("@{expression}")
    assertEquals(true, result)
  }

  @Test
  fun `should not find expression when there is no expression`() {
    val result = containsExpression("This is a text with no expression inside of the text!")
    assertEquals(false, result)
  }

  // #Tests for function "resolve"
  private val defaultLogger = DefaultLogger()
  private val emptyStateHierarchy = emptyList<ServerDrivenState>()
  private val defaultRenderNode = RenderNode(
    "myNode",
    "container",
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
  )

  // ##State Bindings
  @Test
  fun `should replace by state string`() {
    val expectedResult = "Hello World!"
    val stateHierarchy = listOf(ServerDrivenState("sds", expectedResult, defaultRenderNode))
    val result = resolveExpressions("@{sds}", stateHierarchy, defaultOperations, defaultLogger)
    assertEquals(expectedResult, result)
  }

  @Test
  fun `should replace by state number`() {
    val expectedResult = 584
    val stateHierarchy = listOf(ServerDrivenState("sds", expectedResult, defaultRenderNode))
    val result = resolveExpressions("@{sds}", stateHierarchy, defaultOperations, defaultLogger)
    assertEquals(expectedResult, result)
  }

  @Test
  fun `should replace by state float number`() {
    val expectedResult = 584.73
    val stateHierarchy = listOf(ServerDrivenState("sds", expectedResult, defaultRenderNode))
    val result = resolveExpressions("@{sds}", stateHierarchy, defaultOperations, defaultLogger)
    assertEquals(result, expectedResult)
  }

  @Test
  fun `should replace by state boolean`() {
    val expectedResult = true
    val stateHierarchy = listOf(ServerDrivenState("sds", expectedResult, defaultRenderNode))
    val result = resolveExpressions("@{sds}", stateHierarchy, defaultOperations, defaultLogger)
    assertEquals(result, expectedResult)
  }

  @Test
  fun `should replace by state array`() {
    val expectedResult = arrayOf(1, 2, 3, 4)
    val stateHierarchy = listOf(ServerDrivenState("sds", expectedResult, defaultRenderNode))
    val result = resolveExpressions("@{sds}", stateHierarchy, defaultOperations, defaultLogger)
    assertEquals(expectedResult, result)
  }

  @Test
  fun `should replace by state object`() {
    val expectedResult = mapOf(
      "firstName" to "Test",
      "lastName" to "de Oliveira",
      "email" to "testdeoliveira@kotlintest.com"
    )

    val stateHierarchy = listOf(ServerDrivenState("sds", expectedResult, defaultRenderNode))
    val result = resolveExpressions("@{sds}", stateHierarchy, defaultOperations, defaultLogger)
    assertEquals(expectedResult, result)
  }

  @Test
  fun `should replace binding in the middle of a text string`() {
    val stateHierarchy = listOf(ServerDrivenState("sds", "Hello World", defaultRenderNode))
    val result = resolveExpressions(
      "Mid text expression: @{sds}.",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals("Mid text expression: Hello World.", result)
  }

  @Test
  fun `should replace binding in the middle of a text number`() {
    val stateHierarchy = listOf(ServerDrivenState("sds", 584, defaultRenderNode))
    val result = resolveExpressions(
      "Mid text expression: @{sds}.",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals("Mid text expression: 584.", result)
  }

  @Test
  fun `should replace binding in the middle of a text boolean`() {
    val stateHierarchy = listOf(ServerDrivenState("sds", true, defaultRenderNode))
    val result = resolveExpressions(
      "Mid text expression: @{sds}.",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals("Mid text expression: true.", result)
  }

  @Test
  fun `should replace binding in the middle of a text array as string`() {
    val stateValue = arrayOf(1, 2, 3, 4)
    val stateHierarchy = listOf(ServerDrivenState("sds", stateValue, defaultRenderNode))
    val result = resolveExpressions(
      "Mid text expression: @{sds}.",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals("Mid text expression: ${stateValue}.", result)
  }

  @Test
  fun `should replace binding in the middle of a text object as string`() {
    val person = mapOf(
      "firstName" to "Test",
      "lastName" to "de Oliveira",
      "email" to "testdeoliveira@kotlintest.com"
    )

    val stateHierarchy = listOf(ServerDrivenState("sds", person, defaultRenderNode))
    val result = resolveExpressions(
      "Mid text expression: @{sds}.",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals("Mid text expression: $person.", result)
  }

  @Test
  fun `should replace binding in the middle of a text object key`() {
    val person = mapOf(
      "firstName" to "Test",
      "lastName" to "de Oliveira",
      "email" to "testdeoliveira@kotlintest.com"
    )

    val stateHierarchy = listOf(ServerDrivenState("sds", person, defaultRenderNode))
    val result = resolveExpressions(
      "Mid text expression: @{sds.lastName}.",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals("Mid text expression: de Oliveira.", result)
  }

  @Test
  fun `should not replace binding with an array position`() {
    val person = mapOf(
      "firstName" to "Test",
      "lastName" to "de Oliveira",
      "email" to "testdeoliveira@kotlintest.com",
      "phones" to arrayOf("(00) 00000-0000", "(99) 99999-9999")
    )

    val stateHierarchy = listOf(ServerDrivenState("sds", person, defaultRenderNode))
    val result = resolveExpressions("@{sds.phones[1]}", stateHierarchy, defaultOperations, defaultLogger)
    assertEquals(null, result)
  }

  @Test
  fun `should not replace binding in the middle of a text with an array position`() {
    val array = arrayOf("one", "two", "three", "four")
    val stateHierarchy = listOf(ServerDrivenState("sds", array, defaultRenderNode))
    val result = resolveExpressions(
      "Mid text expression: @{sds[2]}.",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals("Mid text expression: .", result)
  }

  @Test
  fun `should replace binding in the middle using multiple states`() {
    val person = mapOf(
      "firstName" to "Test",
      "lastName" to "de Oliveira",
      "email" to "testdeoliveira@kotlintest.com"
    )

    val product = mapOf(
      "productName" to "Test Object",
      "price" to 133.7,
      "description" to "Product for testing"
    )

    val sport = mapOf(
      "sportName" to "Basketball",
      "whatYouUse" to "Ball"
    )

    val stateHierarchy = listOf(
      ServerDrivenState("person", person, defaultRenderNode),
      ServerDrivenState("product", product, defaultRenderNode),
      ServerDrivenState("sport", sport, defaultRenderNode),
    )

    val result = resolveExpressions(
      "Mid text expression: @{product.price}.",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals("Mid text expression: 133.7.", result)
  }

  @Test
  fun `should replace with empty string if no state is found on a string interpolation`() {
    val stateHierarchy = listOf(ServerDrivenState("sds2", "Hello World", defaultRenderNode))
    val result = resolveExpressions(
      "Mid text expression: @{sds}.",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals("Mid text expression: .", result)
  }

  @Test
  fun `should replace with null if no state is found`() {
    val stateHierarchy = listOf(ServerDrivenState("sds2", "Hello World", defaultRenderNode))
    val result = resolveExpressions("@{sds}", stateHierarchy, defaultOperations, defaultLogger)
    assertEquals(null, result)
  }

  @Test
  fun `should not replace if path does not exist in the referred state`() {
    val person = mapOf(
      "firstName" to "Test",
      "lastName" to "de Oliveira",
      "email" to "testdeoliveira@kotlintest.com"
    )

    val stateHierarchy = listOf(ServerDrivenState("sds", person, defaultRenderNode))
    val result = resolveExpressions("@{sds.description}", stateHierarchy, defaultOperations, defaultLogger)
    assertEquals(null, result)
  }

  @Test
  fun `should escape expression`() {
    val stateHierarchy = listOf(ServerDrivenState("sds", "Hello World", defaultRenderNode))
    val result = resolveExpressions("\\@{sds}", stateHierarchy, defaultOperations, defaultLogger)
    assertEquals("@{sds}", result)
  }

  @Test
  fun `should not escape expression when slash is also escaped`() {
    val stateHierarchy = listOf(ServerDrivenState("sds", "Hello World", defaultRenderNode))
    val result = resolveExpressions("\\\\@{sds}", stateHierarchy, defaultOperations, defaultLogger)
    assertEquals("\\Hello World", result)
  }

  @Test
  fun `should not escape expression when a escaped slash is present but a nother slash is also present`() {
    val stateHierarchy = listOf(ServerDrivenState("sds", "Hello World", defaultRenderNode))
    val result = resolveExpressions("\\\\\\@{sds}", stateHierarchy, defaultOperations, defaultLogger)
    assertEquals("\\@{sds}", result)
  }

  // #Literals
  @Test
  fun `should resolve literals`() {
    val stateHierarchy = listOf<ServerDrivenState>()

    var result = resolveExpressions("@{true}", stateHierarchy, defaultOperations, defaultLogger)
    assertEquals(true, result)

    result = resolveExpressions("@{false}", stateHierarchy, defaultOperations, defaultLogger)
    assertEquals(false, result)

    result = resolveExpressions("@{null}", stateHierarchy, defaultOperations, defaultLogger)
    assertEquals(null, result)

    result = resolveExpressions("@{10}", stateHierarchy, defaultOperations, defaultLogger)
    assertEquals(10, result)

    result = resolveExpressions("@{'true'}", stateHierarchy, defaultOperations, defaultLogger)
    assertEquals("true", result)

    result = resolveExpressions(
      "@{'hello world, this is { beagle }!'}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals("hello world, this is { beagle }!", result)
  }

  @Test
  fun `should escape string`() {
    val result = resolveExpressions("@{'hello \\'world\\'!'}", listOf(), defaultOperations, defaultLogger)
    assertEquals("hello 'world'!", result)
  }

  @Test
  fun `should keep control symbols`() {
    val result = resolveExpressions("@{'hello\nworld!'}", listOf(), defaultOperations, defaultLogger)
    assertEquals("hello\nworld!", result)
  }

  @Test
  fun `should do nothing for a malformed string`() {
    val result = resolveExpressions("@{\'test}", listOf(), defaultOperations, defaultLogger)
    assertEquals("@{\'test}", result)
  }

  @Test
  fun `should treat malformed number as a context id`() {
    val stateHierarchy = listOf(ServerDrivenState("5ao1", "test", defaultRenderNode))
    val result = resolveExpressions("@{5ao1}", stateHierarchy, defaultOperations, defaultLogger)
    assertEquals("test", result)
  }

  @Test
  fun `should return null for a malformed number and an invalid context id`() {
    val stateHierarchy = listOf(ServerDrivenState("58.72.98", "test", defaultRenderNode))
    val result = resolveExpressions("@{58.72.98}", stateHierarchy, defaultOperations, defaultLogger)
    assertEquals(null, result)
  }

  // Operations

  @Test
  fun `should evaluate correctly the and operation`() {
    var result = resolveExpressions(
      "@{and(eq(1,1), gt(2,1), lt(2,3))}",
      emptyStateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }

    result = resolveExpressions(
      "@{and(eq(1,1), gt(2,4), lt(2,3))}",
      emptyStateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertFalse { result as Boolean }
  }

  @Test
  fun `should evaluate correctly the capitalize operation`() {
    var stateHierarchy = listOf(ServerDrivenState("sds", "test expression", defaultRenderNode))
    var result = resolveExpressions(
      "@{capitalize(sds)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals("Test expression", result as String)

    stateHierarchy = listOf(ServerDrivenState("sds", "test expression With other letters", defaultRenderNode))
    result = resolveExpressions(
      "@{capitalize(sds)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals("Test expression With other letters", result as String)
  }

  @Test
  fun `should evaluate correctly the concat operation`() {
    val stateHierarchy = listOf(ServerDrivenState("sds", arrayOf("one", "-two-", "three"), defaultRenderNode))
    val result = resolveExpressions(
      "@{concat(sds)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals("one-two-three", result as String)
  }

  @Test
  fun `should evaluate correctly the condition operation`() {
    val stateHierarchy = listOf(
      ServerDrivenState(
        "sds",
        mapOf(
          "number" to 1,
          "valid" to "this is a valid value",
          "invalid" to 13.14
        ),
        defaultRenderNode
      )
    )
    var result = resolveExpressions(
      "@{condition(eq(sds.number, 1), sds.valid, sds.invalid)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals("this is a valid value", result as String)

    result = resolveExpressions(
      "@{condition(eq(sds.number, 2), sds.valid, sds.invalid)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals(13.14, result as Double)

    result = resolveExpressions(
      "@{condition(true, 'valid value', 'fail')}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals("valid value", result as String)

    result = resolveExpressions(
      "@{condition(false, 'valid value', 'fail')}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals("fail", result as String)
  }

  @Test
  fun `should evaluate correctly the contains operation`() {
    val stateHierarchy = listOf(ServerDrivenState("sds", arrayOf("one", "-two-", "three"), defaultRenderNode))
    var result = resolveExpressions(
      "@{contains(sds, '-two-')}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }

    result = resolveExpressions(
      "@{contains(sds, 'four')}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertFalse { result as Boolean }
  }

  @Test
  fun `should evaluate correctly the divide operation`() {
    val stateHierarchy = listOf(ServerDrivenState("sds", arrayOf(16, 2, 2), defaultRenderNode))
    var result = resolveExpressions(
      "@{divide(sds)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals(4, result as Number)

    result = resolveExpressions(
      "@{divide(16, 2, 2)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals(4, result as Number)
  }

  @Test
  fun `should evaluate correctly the eq operation`() {
    val stateHierarchy = listOf(ServerDrivenState("sds", 12, defaultRenderNode))
    var result = resolveExpressions(
      "@{eq(sds, 12)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }

    result = resolveExpressions(
      "@{eq('exp', 'exp')}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }

    result = resolveExpressions(
      "@{eq('exp', 'xp')}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertFalse { result as Boolean }
  }

  @Test
  fun `should evaluate correctly the gt operation`() {
    val stateHierarchy = listOf(ServerDrivenState("sds", 16, defaultRenderNode))
    var result = resolveExpressions(
      "@{gt(sds, 14)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }

    result = resolveExpressions(
      "@{gt(sds, 18)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertFalse { result as Boolean }

    result = resolveExpressions(
      "@{gt(18, 14)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }
  }

  @Test
  fun `should evaluate correctly the gte operation`() {
    val stateHierarchy = listOf(ServerDrivenState("sds", 16, defaultRenderNode))
    var result = resolveExpressions(
      "@{gte(sds, 14)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }

    result = resolveExpressions(
      "@{gte(sds, 18)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertFalse { result as Boolean }

    result = resolveExpressions(
      "@{gte(18, 14)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }

    result = resolveExpressions(
      "@{gte(sds, 16)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }

    result = resolveExpressions(
      "@{gte(16, 16)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }
  }

  @Test
  fun `should evaluate correctly the insert operation`() {
    val stateHierarchy = listOf(ServerDrivenState("sds", arrayOf("one", "-two-", "three"), defaultRenderNode))
    var result = resolveExpressions(
      "@{insert(sds, 'four')}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals(4, (result as MutableList<Any?>).size)
    assertEquals("four", result[3])

    result = resolveExpressions(
      "@{insert(sds, 'four', 1)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals(4, (result as MutableList<Any?>).size)
    assertEquals("one", result[0])
    assertEquals("four", result[1])
    assertEquals("-two-", result[2])
    assertEquals("three", result[3])
  }

  @Test
  fun `should evaluate correctly the isEmpty operation`() {
    var stateHierarchy = listOf(ServerDrivenState("sds", arrayOf("one", "-two-", "three"), defaultRenderNode))
    var result = resolveExpressions(
      "@{isEmpty(sds)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertFalse { result as Boolean }

    stateHierarchy = listOf(ServerDrivenState("sds", arrayOf<String>(), defaultRenderNode))
    result = resolveExpressions(
      "@{isEmpty(sds)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }

    stateHierarchy = listOf(ServerDrivenState("sds", emptyMap<String, String>(), defaultRenderNode))
    result = resolveExpressions(
      "@{isEmpty(sds)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }

    stateHierarchy = listOf(ServerDrivenState("sds", mapOf("hello" to "world"), defaultRenderNode))
    result = resolveExpressions(
      "@{isEmpty(sds)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertFalse { result as Boolean }

    result = resolveExpressions(
      "@{isEmpty('hello world')}",
      emptyStateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertFalse { result as Boolean }

    result = resolveExpressions(
      "@{isEmpty('')}",
      emptyStateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }
  }

  @Test
  fun `should evaluate correctly the isNull operation`() {
    var stateHierarchy = listOf(ServerDrivenState("sds", null, defaultRenderNode))
    var result = resolveExpressions(
      "@{isNull(sds)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }

    stateHierarchy = listOf(ServerDrivenState("sds", "", defaultRenderNode))
    result = resolveExpressions(
      "@{isNull(sds)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertFalse { result as Boolean }

    result = resolveExpressions(
      "@{isNull(null)}",
      emptyStateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }
  }

  @Test
  fun `should evaluate correctly the length operation`() {
    var stateHierarchy = listOf(ServerDrivenState("sds", arrayOf("one", "-two-", "three"), defaultRenderNode))
    var result = resolveExpressions(
      "@{length(sds)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals(3, result as Int)

    stateHierarchy = listOf(ServerDrivenState("sds", "Hello World", defaultRenderNode))
    result = resolveExpressions(
      "@{length(sds)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals(11, result as Int)

    result = resolveExpressions(
      "@{length('Hello World')}",
      emptyStateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals(11, result as Int)

    result = resolveExpressions(
      "@{length(13.14)}",
      emptyStateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals(5, result as Int)
  }

  @Test
  fun `should evaluate correctly the lowercase operation`() {
    val stateHierarchy = listOf(ServerDrivenState("sds", "This is a TEST", defaultRenderNode))
    var result = resolveExpressions(
      "@{lowercase(sds)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals("this is a test", result as String)

    result = resolveExpressions(
      "@{lowercase('This is a TEST')}",
      emptyStateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals("this is a test", result as String)
  }

  @Test
  fun `should evaluate correctly the lt operation`() {
    val stateHierarchy = listOf(ServerDrivenState("sds", 16, defaultRenderNode))
    var result = resolveExpressions(
      "@{lt(sds, 18)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }

    result = resolveExpressions(
      "@{lt(sds, 14)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertFalse { result as Boolean }

    result = resolveExpressions(
      "@{lt(14, 18)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }
  }

  @Test
  fun `should evaluate correctly the lte operation`() {
    val stateHierarchy = listOf(ServerDrivenState("sds", 16, defaultRenderNode))
    var result = resolveExpressions(
      "@{lte(sds, 18)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }

    result = resolveExpressions(
      "@{lte(sds, 14)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertFalse { result as Boolean }

    result = resolveExpressions(
      "@{lte(12, 14)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }

    result = resolveExpressions(
      "@{lte(sds, 16)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }

    result = resolveExpressions(
      "@{lte(16, 16)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }
  }

  @Test
  fun `should evaluate correctly the match operation`() {
    val stateHierarchy = listOf(
      ServerDrivenState(
        "sds",
        mapOf(
          "text" to "This is a {Test} inside a string text",
          "matcher" to """^.*\{.*}?.*$"""
        ),
        defaultRenderNode
      )
    )
    val result = resolveExpressions(
      "@{match(sds.text, sds.matcher)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }
  }

  @Test
  fun `should evaluate correctly the multiply operation`() {
    val stateHierarchy = listOf(ServerDrivenState("sds", arrayOf(16, 2, 2), defaultRenderNode))
    var result = resolveExpressions(
      "@{multiply(sds)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals(64, result as Number)

    result = resolveExpressions(
      "@{multiply(16, 2, 2)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals(64, result as Number)
  }

  @Test
  fun `should evaluate correctly the not operation`() {
    var result = resolveExpressions(
      "@{not(eq(1,2))}",
      emptyStateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }

    result = resolveExpressions(
      "@{not(eq(1,1))}",
      emptyStateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertFalse { result as Boolean }
  }

  @Test
  fun `should evaluate correctly the or operation`() {
    var result = resolveExpressions(
      "@{or(eq(1,2), gt(2,1))}",
      emptyStateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }

    result = resolveExpressions(
      "@{or(eq(1,2), gt(1,2))}",
      emptyStateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertFalse { result as Boolean }

    val stateHierarchy = listOf(ServerDrivenState("sds", arrayOf(false, false, true), defaultRenderNode))
    result = resolveExpressions(
      "@{or(sds)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertTrue { result as Boolean }
  }

  @Test
  fun `should evaluate correctly the remove operation`() {
    val stateHierarchy = listOf(ServerDrivenState("sds", arrayOf("one", "two", "three"), defaultRenderNode))
    val result = resolveExpressions(
      "@{remove(sds, 'two')}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals(2, (result as MutableList<Any?>).size)
    assertEquals("one", result[0])
    assertEquals("three", result[1])
  }

  @Test
  fun `should evaluate correctly the removeIndex operation`() {
    val stateHierarchy = listOf(ServerDrivenState("sds", arrayOf("one", "two", "three"), defaultRenderNode))
    val result = resolveExpressions(
      "@{removeIndex(sds, 1)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals(2, (result as MutableList<Any?>).size)
    assertEquals("one", result[0])
    assertEquals("three", result[1])
  }

  @Test
  fun `should evaluate correctly the replace operation`() {
    val stateHierarchy = listOf(
      ServerDrivenState(
        "sds",
        mapOf(
          "text" to "This is a Test text.",
          "term" to "This is a",
          "new" to "Replaced"
        ),
        defaultRenderNode
      )
    )
    val result = resolveExpressions(
      "@{replace(sds.text, sds.term, sds.new)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals("Replaced Test text.", result as String)
  }

  @Test
  fun `should evaluate correctly the substr operation`() {
    val stateHierarchy = listOf(
      ServerDrivenState(
        "sds",
        mapOf(
          "text" to "This is a Test text.",
          "start" to 10,
          "end" to 14
        ),
        defaultRenderNode
      )
    )
    val result = resolveExpressions(
      "@{substr(sds.text, sds.start, sds.end)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals("Test", result as String)
  }

  @Test
  fun `should evaluate correctly the subtract operation`() {
    val stateHierarchy = listOf(ServerDrivenState("sds", arrayOf(16, 2, 2), defaultRenderNode))
    var result = resolveExpressions(
      "@{subtract(sds)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals(12, result as Number)

    result = resolveExpressions(
      "@{subtract(16, 2, 2)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals(12, result as Number)
  }

  @Test
  fun `should evaluate correctly the sum operation`() {
    val stateHierarchy = listOf(ServerDrivenState("sds", arrayOf(16, 2, 2), defaultRenderNode))
    var result = resolveExpressions(
      "@{sum(sds)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals(20, result as Number)

    result = resolveExpressions(
      "@{sum(16, 2, 2)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals(20, result as Number)
  }

  @Test
  fun `should evaluate correctly the union operation`() {
    val stateHierarchy = listOf(
      ServerDrivenState(
        "sds",
        mapOf(
          "first" to arrayOf("one", "two", "three"),
          "second" to arrayOf("four", "five", "six"),
        ),
        defaultRenderNode
      )
    )
    val result = resolveExpressions(
      "@{union(sds.first, sds.second)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals(6, (result as MutableList<Any?>).size)
    assertEquals("one", result[0])
    assertEquals("two", result[1])
    assertEquals("three", result[2])
    assertEquals("four", result[3])
    assertEquals("five", result[4])
    assertEquals("six", result[5])
  }

  @Test
  fun `should evaluate correctly the uppercase operation`() {
    val stateHierarchy = listOf(ServerDrivenState("sds", "This is a TEST", defaultRenderNode))
    var result = resolveExpressions(
      "@{uppercase(sds)}",
      stateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals("THIS IS A TEST", result as String)

    result = resolveExpressions(
      "@{uppercase('This is a TEST')}",
      emptyStateHierarchy,
      defaultOperations,
      defaultLogger
    )
    assertEquals("THIS IS A TEST", result as String)
  }
}
