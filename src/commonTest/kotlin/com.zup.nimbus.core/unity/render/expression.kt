package com.zup.nimbus.core.unity.render

import com.zup.nimbus.core.render.containsExpression
import com.zup.nimbus.core.render.resolveExpressions
import com.zup.nimbus.core.tree.RenderNode
import com.zup.nimbus.core.tree.ServerDrivenState
import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionTest {
  // #Tests for function "containsExpression"
  @Test
  fun shouldFindExpressionInsideText() {
    val result = containsExpression("This is a text with an @{expression} inside of the text!")
    assertEquals(true, result)
  }

  @Test
  fun shouldFindExpressionWhenExpressionIsWholeText() {
    val result = containsExpression("@{expression}")
    assertEquals(true, result)
  }

  @Test
  fun shouldNotFindExpressionWhenThereIsNoExpression() {
    val result = containsExpression("This is a text with no expression inside of the text!")
    assertEquals(false, result)
  }

  // #Tests for function "resolve"
  private val defaultRenderNode = RenderNode(
    "myNode",
    "container",
    null,
    null,
    null,
    null,
    null,
    null
  )

  // ##State Bindings
  @Test
  fun shouldReplaceByStateString() {
    val expectedResult = "Hello World!"
    val stateHierarchy = listOf(ServerDrivenState("sds", expectedResult, defaultRenderNode))
    val result = resolveExpressions("@{sds}", stateHierarchy, null, null)
    assertEquals(expectedResult, result)
  }

  @Test
  fun shouldReplaceByStateNumber() {
    val expectedResult = 584
    val stateHierarchy = listOf(ServerDrivenState("sds", expectedResult, defaultRenderNode))
    val result = resolveExpressions("@{sds}", stateHierarchy, null, null)
    assertEquals(expectedResult, result)
  }

  @Test
  fun shouldReplaceByStateFloatNumber() {
    val expectedResult = 584.73
    val stateHierarchy = listOf(ServerDrivenState("sds", expectedResult, defaultRenderNode))
    val result = resolveExpressions("@{sds}", stateHierarchy, null, null)
    assertEquals(result, expectedResult)
  }

  @Test
  fun shouldReplaceByStateBoolean() {
    val expectedResult = true
    val stateHierarchy = listOf(ServerDrivenState("sds", expectedResult, defaultRenderNode))
    val result = resolveExpressions("@{sds}", stateHierarchy, null, null)
    assertEquals(result, expectedResult)
  }

  @Test
  fun shouldReplaceByStateArray() {
    val expectedResult = arrayOf(1, 2, 3, 4)
    val stateHierarchy = listOf(ServerDrivenState("sds", expectedResult, defaultRenderNode))
    val result = resolveExpressions("@{sds}", stateHierarchy, null, null)
    assertEquals(expectedResult, result)
  }

  @Test
  fun shouldReplaceByStateObject() {
    val expectedResult = mapOf(
      "firstName" to "Test",
      "lastName" to "de Oliveira",
      "email" to "testdeoliveira@kotlintest.com"
    )

    val stateHierarchy = listOf(ServerDrivenState("sds", expectedResult, defaultRenderNode))
    val result = resolveExpressions("@{sds}", stateHierarchy, null, null)
    assertEquals(expectedResult, result)
  }

  @Test
  fun shouldReplaceBindingInTheMiddleOfTextString() {
    val stateHierarchy = listOf(ServerDrivenState("sds", "Hello World", defaultRenderNode))
    val result = resolveExpressions("Mid text expression: @{sds}.", stateHierarchy, null, null)
    assertEquals("Mid text expression: Hello World.", result)
  }

  @Test
  fun shouldReplaceBindingInTheMiddleOfTextNumber() {
    val stateHierarchy = listOf(ServerDrivenState("sds", 584, defaultRenderNode))
    val result = resolveExpressions("Mid text expression: @{sds}.", stateHierarchy, null, null)
    assertEquals("Mid text expression: 584.", result)
  }

  @Test
  fun shouldReplaceBindingInTheMiddleOfTextBoolean() {
    val stateHierarchy = listOf(ServerDrivenState("sds", true, defaultRenderNode))
    val result = resolveExpressions("Mid text expression: @{sds}.", stateHierarchy, null, null)
    assertEquals("Mid text expression: true.", result)
  }

  @Test
  fun shouldReplaceBindingInTheMiddleOfTextArrayAsString() {
    val stateValue = arrayOf(1, 2, 3, 4)
    val stateHierarchy = listOf(ServerDrivenState("sds", stateValue, defaultRenderNode))
    val result = resolveExpressions("Mid text expression: @{sds}.", stateHierarchy, null, null)
    assertEquals("Mid text expression: ${stateValue}.", result)
  }

  @Test
  fun shouldReplaceBindingInTheMiddleOfTextObjectAsString() {
    val person = mapOf(
      "firstName" to "Test",
      "lastName" to "de Oliveira",
      "email" to "testdeoliveira@kotlintest.com"
    )

    val stateHierarchy = listOf(ServerDrivenState("sds", person, defaultRenderNode))
    val result = resolveExpressions("Mid text expression: @{sds}.", stateHierarchy, null, null)
    assertEquals("Mid text expression: $person.", result)
  }

  @Test
  fun shouldReplaceBindingInTheMiddleOfTextObjectKey() {
    val person = mapOf(
      "firstName" to "Test",
      "lastName" to "de Oliveira",
      "email" to "testdeoliveira@kotlintest.com"
    )

    val stateHierarchy = listOf(ServerDrivenState("sds", person, defaultRenderNode))
    val result = resolveExpressions("Mid text expression: @{sds.lastName}.", stateHierarchy, null, null)
    assertEquals("Mid text expression: de Oliveira.", result)
  }

  @Test
  fun shouldNotReplaceBindingWithArrayPosition() {
    val person = mapOf(
      "firstName" to "Test",
      "lastName" to "de Oliveira",
      "email" to "testdeoliveira@kotlintest.com",
      "phones" to arrayOf("(00) 00000-0000", "(99) 99999-9999")
    )

    val stateHierarchy = listOf(ServerDrivenState("sds", person, defaultRenderNode))
    val result = resolveExpressions("@{sds.phones[1]}", stateHierarchy, null, null)
    assertEquals("@{sds.phones[1]}", result)
  }

  @Test
  fun shouldNotReplaceBindingInTheMiddleOfTextWithArrayPosition() {
    val array = arrayOf("one", "two", "three", "four")
    val stateHierarchy = listOf(ServerDrivenState("sds", array, defaultRenderNode))
    val result = resolveExpressions("Mid text expression: @{sds[2]}.", stateHierarchy, null, null)
    assertEquals("Mid text expression: .", result)
  }

  @Test
  fun shouldReplaceBindingInTheMiddleUsingMultipleStates() {
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

    val result = resolveExpressions("Mid text expression: @{product.price}.", stateHierarchy, null, null)
    assertEquals("Mid text expression: 133.7.", result)
  }

  @Test
  fun shouldReplaceWithEmptyStringIfNotStateIsFoundOnStringInterpolation() {
    val stateHierarchy = listOf(ServerDrivenState("sds2", "Hello World", defaultRenderNode))
    val result = resolveExpressions("Mid text expression: @{sds}.", stateHierarchy, null, null)
    assertEquals("Mid text expression: .", result)
  }

  @Test
  fun shouldReplaceWithNullIfNoStateIsFound() {
    val stateHierarchy = listOf(ServerDrivenState("sds2", "Hello World", defaultRenderNode))
    val result = resolveExpressions("@{sds}", stateHierarchy, null, null)
    assertEquals(null, result)
  }

  @Test
  fun shouldNotReplaceIfPathDoesNotExistInTheReferredState() {
    val person = mapOf(
      "firstName" to "Test",
      "lastName" to "de Oliveira",
      "email" to "testdeoliveira@kotlintest.com"
    )

    val stateHierarchy = listOf(ServerDrivenState("sds", person, defaultRenderNode))
    val result = resolveExpressions("@{sds.description}", stateHierarchy, null, null)
    assertEquals("@{sds.description}", result)
  }

  @Test
  fun shouldEscapeExpression() {
    val stateHierarchy = listOf(ServerDrivenState("sds", "Hello World", defaultRenderNode))
    val result = resolveExpressions("\\@{sds}", stateHierarchy, null, null)
    assertEquals("@{sds}", result)
  }

  @Test
  fun shouldNotEscapeExpressionWhenSlashIsAlsoEscaped() {
    val stateHierarchy = listOf(ServerDrivenState("sds", "Hello World", defaultRenderNode))
    val result = resolveExpressions("\\\\@{sds}", stateHierarchy, null, null)
    assertEquals("\\Hello World", result)
  }

  @Test
  fun shouldNotEscapeExpressionWhenAEscapedSlashIsPresentButAnotherSlashIsAlsoPresent() {
    val stateHierarchy = listOf(ServerDrivenState("sds", "Hello World", defaultRenderNode))
    val result = resolveExpressions("\\\\\\@{sds}", stateHierarchy, null, null)
    assertEquals("\\@{sds}", result)
  }

  // #Literals
  @Test
  fun shouldResolveLiterals() {
    val stateHierarchy = listOf<ServerDrivenState>()

    var result = resolveExpressions("@{true}", stateHierarchy, null, null)
    assertEquals(true, result)

    result = resolveExpressions("@{false}", stateHierarchy, null, null)
    assertEquals(false, result)

    result = resolveExpressions("@{null}", stateHierarchy, null, null)
    assertEquals(null, result)

    result = resolveExpressions("@{10}", stateHierarchy, null, null)
    assertEquals(10f, result)

    result = resolveExpressions("@{'true'}", stateHierarchy, null, null)
    assertEquals("true", result)

    result = resolveExpressions("@{'hello world, this is { beagle }!'}", stateHierarchy, null, null)
    assertEquals("hello world, this is { beagle }!", result)
  }

  @Test
  fun shouldEscapeString() {
    val result = resolveExpressions("@{'hello \\'world\\'!'}", listOf(), null, null)
    assertEquals("hello 'world'!", result)
  }

  @Test
  fun shouldKeepControlSymbols() {
    val result = resolveExpressions("@{'hello\nworld!'}", listOf(), null, null)
    assertEquals("hello\nworld!", result)
  }

  @Test
  fun shouldDoNothingForMalformedString() {
    val result = resolveExpressions("@{\'test}", listOf(), null, null)
    assertEquals("@{\'test}", result)
  }

  @Test
  fun shouldTreatMalformedNumberAsContextId() {
    val stateHierarchy = listOf(ServerDrivenState("5o1", "test", defaultRenderNode))
    val result = resolveExpressions("@{5o1}", stateHierarchy, null, null)
    assertEquals("test", result)
  }

  @Test
  fun shouldReturnNullForMalformedNumberAndInvalidContextId() {
    val stateHierarchy = listOf(ServerDrivenState("58.72.98", "test", defaultRenderNode))
    val result = resolveExpressions("@{58.72.98}", stateHierarchy, null, null)
    assertEquals(null, result)
  }
}
