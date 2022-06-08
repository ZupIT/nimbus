package com.zup.nimbus.core.unity.render

import com.zup.nimbus.core.OperationHandler
import com.zup.nimbus.core.log.DefaultLogger
import com.zup.nimbus.core.render.containsExpression
import com.zup.nimbus.core.render.resolveExpressions
import com.zup.nimbus.core.tree.RenderNode
import com.zup.nimbus.core.tree.ServerDrivenState
import kotlin.test.Test
import kotlin.test.assertEquals

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
  private val defaultOperations = emptyMap<String, OperationHandler>()
  private val defaultLogger = DefaultLogger()
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
    assertEquals("@{sds.phones[1]}", result)
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
    assertEquals("@{sds.description}", result)
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
    assertEquals(10f, result)

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
    val stateHierarchy = listOf(ServerDrivenState("5o1", "test", defaultRenderNode))
    val result = resolveExpressions("@{5o1}", stateHierarchy, defaultOperations, defaultLogger)
    assertEquals("test", result)
  }

  @Test
  fun `should return null for a malformed number and an invalid context id`() {
    val stateHierarchy = listOf(ServerDrivenState("58.72.98", "test", defaultRenderNode))
    val result = resolveExpressions("@{58.72.98}", stateHierarchy, defaultOperations, defaultLogger)
    assertEquals(null, result)
  }
}
