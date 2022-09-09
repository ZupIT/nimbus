package com.zup.nimbus.core.expression

import com.zup.nimbus.core.regex.toFastRegex
import com.zup.nimbus.core.scope.NimbusScope
import com.zup.nimbus.core.tree.stateful.Stateful

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
      ?: throw Error("wrong format for parameters: $parameterString")
    parameters.add(match.removeSuffix(",").trim())
    position += match.length
  }

  return parameters
}

class OperationParser(private val scope: NimbusScope) {
  fun parse(code: String, origin: Stateful): Expression {
    val match = operationRegex.findWithGroups(code)

    if (match == null) {
      scope.getLogger().error("invalid operation in expression: $code. Using null as its value.")
      return Literal(null)
    }

    val (operationName, paramString) = match.destructured
    val operationHandler = scope.getUILibraryManager().getOperation(operationName)
    if (operationHandler == null) {
      scope.getLogger().error("operation with name \"$operationName\" doesn't exist. Using null as its value.")
      return Literal(null)
    }

    val params = parseParameters(paramString)
    val resolvedParams = params.map { param ->
      scope.getExpressionParser().parseExpression(param, origin)
    }

    return Operation(operationHandler, resolvedParams)
  }
}
