package br.com.zup.nimbus.core.unity.expression

import br.com.zup.nimbus.core.EmptyHttpClient
import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.ServerDrivenConfig
import br.com.zup.nimbus.core.ServerDrivenState
import br.com.zup.nimbus.core.expression.Operation
import br.com.zup.nimbus.core.expression.StateReference
import br.com.zup.nimbus.core.expression.StringTemplate
import br.com.zup.nimbus.core.expression.parser.ExpressionParser
import br.com.zup.nimbus.core.scope.StateOnlyScope
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExpressionTest {
  private val nimbus = Nimbus(ServerDrivenConfig(baseUrl = "", platform = "test", httpClient = EmptyHttpClient))
  private val parser = ExpressionParser(nimbus)

  // #Tests for function "containsExpression"
  @Test
  fun `should find expression inside a text`() {
    val contains = parser.containsExpression("This is a text with an @{expression} inside of the text!")
    assertEquals(true, contains)
  }

  @Test
  fun `should find expression when expression is whole text`() {
    val contains = parser.containsExpression("@{expression}")
    assertEquals(true, contains)
  }

  @Test
  fun `should not find expression when there is no expression`() {
    val contains = parser.containsExpression("This is a text with no expression inside of the text!")
    assertEquals(false, contains)
  }

  // ##State Bindings
  @Test
  fun `should replace by state string`() {
    val expectedResult = "Hello World!"
    val scope = StateOnlyScope(
      parent = nimbus,
      states = listOf(ServerDrivenState("sds", expectedResult)),
    )
    val expression = parser.parseString("@{sds}") as StateReference
    expression.initialize(scope)
    assertEquals(expectedResult, expression.getValue())
  }

  @Test
  fun `should replace by state number`() {
    val expectedResult = 584
    val scope = StateOnlyScope(
      parent = nimbus,
      states = listOf(ServerDrivenState("sds", expectedResult)),
    )
    val expression = parser.parseString("@{sds}") as StateReference
    expression.initialize(scope)
    assertEquals(expectedResult, expression.getValue())
  }

  @Test
  fun `should replace by state float number`() {
    val expectedResult = 584.73
    val scope = StateOnlyScope(
      parent = nimbus,
      states = listOf(ServerDrivenState("sds", expectedResult)),
    )
    val expression = parser.parseString("@{sds}") as StateReference
    expression.initialize(scope)
    assertEquals(expectedResult, expression.getValue())
  }

  @Test
  fun `should replace by state boolean`() {
    val expectedResult = true
    val scope = StateOnlyScope(
      parent = nimbus,
      states = listOf(ServerDrivenState("sds", expectedResult)),
    )
    val expression = parser.parseString("@{sds}") as StateReference
    expression.initialize(scope)
    assertEquals(expectedResult, expression.getValue())
  }

  @Test
  fun `should replace by state array`() {
    val expectedResult = listOf(1, 2, 3, 4)
    val scope = StateOnlyScope(
      parent = nimbus,
      states = listOf(ServerDrivenState("sds", expectedResult)),
    )
    val expression = parser.parseString("@{sds}") as StateReference
    expression.initialize(scope)
    assertEquals(expectedResult, expression.getValue())
  }

  @Test
  fun `should replace by state object`() {
    val expectedResult = mapOf(
      "firstName" to "Test",
      "lastName" to "de Oliveira",
      "email" to "testdeoliveira@kotlintest.com"
    )

    val scope = StateOnlyScope(
      parent = nimbus,
      states = listOf(ServerDrivenState("sds", expectedResult)),
    )
    val expression = parser.parseString("@{sds}") as StateReference
    expression.initialize(scope)
    assertEquals(expectedResult, expression.getValue())
  }

  @Test
  fun `should replace binding in the middle of a text string`() {
    val scope = StateOnlyScope(
      parent = nimbus,
      states = listOf(ServerDrivenState("sds", "Hello World")),
    )
    val expression = parser.parseString("Mid text expression: @{sds}.") as StringTemplate
    expression.initialize(scope)
    assertEquals("Mid text expression: Hello World.", expression.getValue())
  }

  @Test
  fun `should replace binding in the middle of a text number`() {
    val scope = StateOnlyScope(
      parent = nimbus,
      states = listOf(ServerDrivenState("sds", 584)),
    )
    val expression = parser.parseString("Mid text expression: @{sds}.") as StringTemplate
    expression.initialize(scope)
    assertEquals("Mid text expression: 584.", expression.getValue())
  }

  @Test
  fun `should replace binding in the middle of a text boolean`() {
    val scope = StateOnlyScope(
      parent = nimbus,
      states = listOf(ServerDrivenState("sds", true)),
    )
    val expression = parser.parseString("Mid text expression: @{sds}.") as StringTemplate
    expression.initialize(scope)
    assertEquals("Mid text expression: true.", expression.getValue())
  }

  @Test
  fun `should replace binding in the middle of a text array as string`() {
    val stateValue = listOf(1, 2, 3, 4)
    val scope = StateOnlyScope(
      parent = nimbus,
      states = listOf(ServerDrivenState("sds", stateValue)),
    )
    val expression = parser.parseString("Mid text expression: @{sds}.") as StringTemplate
    expression.initialize(scope)
    assertEquals("Mid text expression: ${stateValue}.", expression.getValue())
  }

  @Test
  fun `should replace binding in the middle of a text object as string`() {
    val person = mapOf(
      "firstName" to "Test",
      "lastName" to "de Oliveira",
      "email" to "testdeoliveira@kotlintest.com"
    )
    val scope = StateOnlyScope(
      parent = nimbus,
      states = listOf(ServerDrivenState("sds", person)),
    )
    val expression = parser.parseString("Mid text expression: @{sds}.") as StringTemplate
    expression.initialize(scope)
    assertEquals("Mid text expression: $person.", expression.getValue())
  }

  @Test
  fun `should replace binding in the middle of a text object key`() {
    val person = mapOf(
      "firstName" to "Test",
      "lastName" to "de Oliveira",
      "email" to "testdeoliveira@kotlintest.com"
    )
    val scope = StateOnlyScope(
      parent = nimbus,
      states = listOf(ServerDrivenState("sds", person)),
    )
    val expression = parser.parseString("Mid text expression: @{sds.lastName}.") as StringTemplate
    expression.initialize(scope)
    assertEquals("Mid text expression: de Oliveira.", expression.getValue())
  }

  @Test
  fun `should replace binding with an array position`() {
    val person = mapOf(
      "firstName" to "Test",
      "lastName" to "de Oliveira",
      "email" to "testdeoliveira@kotlintest.com",
      "phones" to listOf("(00) 00000-0000", "(99) 99999-9999")
    )
    val scope = StateOnlyScope(
      parent = nimbus,
      states = listOf(ServerDrivenState("sds", person)),
    )
    val expression = parser.parseString("@{sds.phones[1]}") as StateReference
    expression.initialize(scope)
    assertEquals("(99) 99999-9999", expression.getValue())
  }

  @Test
  fun `should not replace binding in the middle of a text with an array position`() {
    val array = listOf("one", "two", "three", "four")
    val scope = StateOnlyScope(
      parent = nimbus,
      states = listOf(ServerDrivenState("sds", array)),
    )
    val expression = parser.parseString("Mid text expression: @{sds[2]}.") as StringTemplate
    expression.initialize(scope)
    assertEquals("Mid text expression: three.", expression.getValue())
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

    val scope = StateOnlyScope(
      parent = nimbus,
      states = listOf(
        ServerDrivenState("person", person),
        ServerDrivenState("product", product),
        ServerDrivenState("sport", sport),
      ),
    )

    val expression = parser.parseString("Mid text expression: @{product.price}.") as StringTemplate
    expression.initialize(scope)
    assertEquals("Mid text expression: 133.7.", expression.getValue())
  }

  @Test
  fun `should replace with empty string if no state is found on a string interpolation`() {
    val scope = StateOnlyScope(
      parent = nimbus,
      states = listOf(ServerDrivenState("sds2", "Hello World")),
    )
    val expression = parser.parseString("Mid text expression: @{sds}.") as StringTemplate
    expression.initialize(scope)
    assertEquals("Mid text expression: .", expression.getValue())
  }

  @Test
  fun `should replace with null if no state is found`() {
    val scope = StateOnlyScope(
      parent = nimbus,
      states = listOf(ServerDrivenState("sds2", "Hello World")),
    )
    val expression = parser.parseString("@{sds}") as StateReference
    expression.initialize(scope)
    assertEquals(null, expression.getValue())
  }

  @Test
  fun `should not replace if path does not exist in the referred state`() {
    val person = mapOf(
      "firstName" to "Test",
      "lastName" to "de Oliveira",
      "email" to "testdeoliveira@kotlintest.com"
    )
    val scope = StateOnlyScope(
      parent = nimbus,
      states = listOf(ServerDrivenState("sds", person)),
    )
    val expression = parser.parseString("@{sds.description}") as StateReference
    expression.initialize(scope)
    assertEquals(null, expression.getValue())
  }

  @Test
  fun `should escape expression`() {
    val scope = StateOnlyScope(
      parent = nimbus,
      states = listOf(ServerDrivenState("sds", "Hello World")),
    )
    val expression = parser.parseString("\\@{sds}") as StringTemplate
    expression.initialize(scope)
    assertEquals("@{sds}", expression.getValue())
  }

  @Test
  fun `should not escape expression when slash is also escaped`() {
    val scope = StateOnlyScope(
      parent = nimbus,
      states = listOf(ServerDrivenState("sds", "Hello World")),
    )
    val expression = parser.parseString("\\\\@{sds}") as StringTemplate
    expression.initialize(scope)
    assertEquals("\\Hello World", expression.getValue())
  }

  @Test
  fun `should not escape expression when a escaped slash is present but another slash is also present`() {
    val scope = StateOnlyScope(
      parent = nimbus,
      states = listOf(ServerDrivenState("sds", "Hello World")),
    )
    val expression = parser.parseString("\\\\\\@{sds}") as StringTemplate
    expression.initialize(scope)
    assertEquals("\\@{sds}", expression.getValue())
  }

  // #Literals
  @Test
  fun `should resolve literals`() {
    var expression = parser.parseString("@{true}")
    assertEquals(true, expression.getValue())

    expression = parser.parseString("@{false}")
    assertEquals(false, expression.getValue())

    expression = parser.parseString("@{null}")
    assertEquals(null, expression.getValue())

    expression = parser.parseString("@{10}")
    assertEquals(10, expression.getValue())

    expression = parser.parseString("@{'true'}")
    assertEquals("true", expression.getValue())

    expression = parser.parseString("@{'hello world, this is { beagle }!'}")
    assertEquals("hello world, this is { beagle }!", expression.getValue())
  }

  @Test
  fun `should escape string`() {
    val expression = parser.parseString("@{'hello \\'world\\'!'}")
    assertEquals("hello 'world'!", expression.getValue())
  }

  @Test
  fun `should keep control symbols`() {
    val expression = parser.parseString("@{'hello\nworld!'}")
    assertEquals("hello\nworld!", expression.getValue())
  }

  @Test
  fun `should do nothing for a malformed string`() {
    val expression = parser.parseString("@{\'test}") as StringTemplate
    expression.initialize(nimbus)
    assertEquals("@{\'test}", expression.getValue())
  }

  @Test
  fun `should treat malformed number as a context id`() {
    val scope = StateOnlyScope(
      parent = nimbus,
      states = listOf(ServerDrivenState("5ao1", "test")),
    )
    val expression = parser.parseString("@{5ao1}") as StateReference
    expression.initialize(scope)
    assertEquals("test", expression.getValue())
  }

  @Test
  fun `should return null for a malformed number and an invalid context id`() {
    val scope = StateOnlyScope(
      parent = nimbus,
      states = listOf(ServerDrivenState("58.72.98", "test")),
    )
    val expression = parser.parseString("@{58.72.98}") as StateReference
    expression.initialize(scope)
    assertEquals(null, expression.getValue())
  }

  // Operations

  @Test
  fun `should evaluate correctly an operation`() {
    var expression = parser.parseString("@{and(eq(1,1), gt(2,1), lt(2,3))}") as Operation
    expression.initialize(nimbus)
    assertTrue(expression.getValue() as Boolean)
    expression = parser.parseString("@{and(eq(1,1), gt(2,4), lt(2,3))}") as Operation
    expression.initialize(nimbus)
    assertFalse(expression.getValue() as Boolean)
  }
}

