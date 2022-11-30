package br.com.zup.nimbus.core.expression.parser

import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.expression.Expression
import br.com.zup.nimbus.core.expression.Literal
import br.com.zup.nimbus.core.expression.Operation
import br.com.zup.nimbus.core.regex.toFastRegex

private val operationRegex = """^(\w+)\((.*)\)$""".toFastRegex()

private val dpaTransitions: Map<String, List<Transition>> = mapOf(
  "initial" to listOf(
    Transition(""",|$""", true, null, null, "final"), // end of parameter
    Transition("(", "(", null, "insideParameterList"), // start of a parameter list
    Transition("""'([^']|(\\.))*'""", true, null, null, "initial"), // strings
    Transition("""[^)]""", true, null, null, "initial"), // general symbols
  ),
  "insideParameterList" to listOf(
    Transition("(", "(", null, "insideParameterList"), // start of another parameter list
    // end of a parameter list, check if still inside a parameter list
    Transition(")", null, "(", "isParameterListOver"),
    Transition("""'([^']|(\\.))*'""", true, null, null, "insideParameterList"), // strings
    Transition(".", true, null, null, "insideParameterList"), // general symbols
  ),
  "isParameterListOver" to listOf(
    Transition(null, DPA.Symbols.EMPTY, "initial"), // end of parameter list, go back to initial state
    // still inside a parameter list, go back to state "insideParameterList"
    Transition(null, null, "insideParameterList"),
  ),
)

private val parameterMatcher = DPA("initial", "final", dpaTransitions)

private fun parseParameters(parameterString: String): List<String> {
  val parameters = mutableListOf<String>()
  var position = 0

  while (position < parameterString.length) {
    val match = parameterMatcher.match(parameterString.substring(position))
      ?: throw IllegalArgumentException("wrong format for parameters: $parameterString")
    parameters.add(match.removeSuffix(",").trim())
    position += match.length
  }

  return parameters
}

class OperationParser(private val nimbus: Nimbus) {
  @Suppress("ReturnCount")
  fun parse(code: String): Expression {
    val match = operationRegex.findWithGroups(code)

    if (match == null) {
      nimbus.logger.error("Invalid operation in expression: $code. Using null as its value.")
      return Literal(null)
    }

    val (operationName, paramString) = match.destructured
    val operationHandler = nimbus.uiLibraryManager.getOperation(operationName)
    if (operationHandler == null) {
      nimbus.logger.error("Operation with name \"$operationName\" doesn't exist. Using null as its value.")
      return Literal(null)
    }

    return try {
      val params = parseParameters(paramString)
      val resolvedParams = params.map { param ->
        nimbus.expressionParser.parseExpression(param)
      }
      Operation(operationHandler, resolvedParams)
    } catch (e: IllegalArgumentException) {
      nimbus.logger.error(e.message ?: "Error while parsing expression.")
      Literal(null)
    }
  }
}
