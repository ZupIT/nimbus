@file:Suppress("TooGenericExceptionThrown") // todo: verify
package com.zup.nimbus.core.render

import com.zup.nimbus.core.OperationHandler
import com.zup.nimbus.core.log.Logger
import com.zup.nimbus.core.regex.toFastRegex
import com.zup.nimbus.core.regex.matches
import com.zup.nimbus.core.regex.replace
import com.zup.nimbus.core.tree.ServerDrivenState
import com.zup.nimbus.core.utils.untypedValueOfPath

//Do not remove the Redundant character escape '\}' in RegExp, this causes error when using android regex implementation
private val expressionRegex = """(\\*)@\{(([^'}]|('([^'\\]|\\.)*'))*)\}""".toFastRegex()
private val fullMatchExpressionRegex = """^@\{(([^'}]|('([^'\\]|\\.)*'))*)\}$""".toFastRegex()
private val literalRegex = """^\d+((.)|(.\d+)?)$""".toFastRegex()
private val operationRegex = """^(\w+)\((.*)\)$""".toFastRegex()
private val stateReferenceRegex = """^[\w\d_]+(\[\d+\])*(\.([\w\d_]+(\[\d+\])*))*$""".toFastRegex()
private val pathRegex = """^([^\.\[\]]+)\.?(.*)""".toFastRegex()

private val dpaTransitions: Map<String, List<Transition>> = mapOf(
  "initial" to listOf(
    Transition(""",|$""".toFastRegex(), null, null, "final"), // end of parameter
    Transition("(", "(", null, "insideParameterList"), // start of a parameter list
    Transition("""'([^']|(\\.))*'""".toFastRegex(), null, null, "initial"), // strings
    Transition("""[^)]""".toFastRegex(), null, null, "initial"), // general symbols
  ),
  "insideParameterList" to listOf(
    Transition("(", "(", null, "insideParameterList"), // start of another parameter list
    // end of a parameter list, check if still inside a parameter list
    Transition(")", null, "(", "isParameterListOver"),
    Transition("""'([^']|(\\.))*'""".toFastRegex(), null, null, "insideParameterList"), // strings
    Transition(".".toFastRegex(), null, null, "insideParameterList"), // general symbols
  ),
  "isParameterListOver" to listOf(
    Transition(null, DPA.Symbols.EMPTY, "initial"), // end of parameter list, go back to initial state
    // still inside a parameter list, go back to state "insideParameterList"
    Transition(null, null, "insideParameterList"),
  ),
)
private val dpa = DPA("initial", "final", dpaTransitions)

private fun parseParameters(parameterString: String): List<String> {
  val parameters: MutableList<String> = mutableListOf()
  var position = 0

  while (position < parameterString.length) {
    val match = dpa.match(parameterString.substring(position))
      ?: throw Error("wrong format for parameters: $parameterString")
    parameters.add(match.removeSuffix(",").trim())
    position += match.length
  }

  return parameters
}

private fun getStateValue(path: String, stateHierarchy: List<ServerDrivenState>, logger: Logger): Any? {
  if (!path.matches(stateReferenceRegex)) {
    throw Error("invalid path \"$path\". Please, make sure your variable names contain only letters, numbers and the " +
      "symbol \"_\". To access substructures use \".\" and to access array indexes use \"[index]\".")
  }

  val pathMatch = pathRegex.findWithGroups(path) ?: return null
  val (stateId, statePath) = pathMatch.destructured

  if (stateId == "null") return null

  val state = stateHierarchy.find { it.id == stateId } ?: throw Error("Couldn't find state with id \"$stateId\"")

  if (statePath.isNotEmpty() && statePath.isNotBlank()) {
    return try {
      return untypedValueOfPath(state.value, statePath)
    } catch (error: Throwable) {
      error.message?.let {
        logger.warn(it)
      }
      null
    }
  }
  return state.value
}

private fun getLiteralValue(literal: String): Any? {
  when (literal) {
    "true" -> return true
    "false" -> return false
    "null" -> return null
  }

  if (literal.matches(literalRegex)) {
    if (literal.contains(".")) return literal.toDouble()
    return literal.toInt()
  }

  if (literal.startsWith("'") && literal.endsWith("'")) {
    return literal.drop(1).dropLast(1).replace("\\'", "'")
  }

  return null
}

private fun getOperationValue(
  operation: String,
  stateHierarchy: List<ServerDrivenState>,
  operationHandlers: Map<String, OperationHandler>,
  logger: Logger,
): Any? {
  val match = operationRegex.findWithGroups(operation)
    ?: throw Error("invalid operation in expression: $operation")

  val (operationName, paramString) = match.destructured
  if (operationHandlers[operationName] == null) {
    throw Error("operation with name \"$operationName\" doesn't exist.")
  }

  val params = parseParameters(paramString)
  val resolvedParams = params.map { param ->
    evaluateExpression(param, stateHierarchy, operationHandlers, logger)
  }.toTypedArray()

  val operationHandler = operationHandlers[operationName]
  if (operationHandler != null) {
    return operationHandler(resolvedParams)
  }
  return null
}

private fun evaluateExpression(
  expression: String,
  stateHierarchy: List<ServerDrivenState>,
  operationHandlers: Map<String, OperationHandler>,
  logger: Logger,
): Any? {
  val literalValue = getLiteralValue(expression)
  if (literalValue != null) return literalValue

  val isOperation = expression.contains("(")
  if (isOperation) return getOperationValue(expression, stateHierarchy, operationHandlers, logger)

  return getStateValue(expression, stateHierarchy, logger)
}

fun containsExpression(value: String): Boolean {
  return expressionRegex.containsMatchIn(value)
}

fun resolveExpressions(
  value: String,
  stateHierarchy: List<ServerDrivenState>,
  operationHandlers: Map<String, OperationHandler>,
  logger: Logger,
): Any? {
  val fullMatch = fullMatchExpressionRegex.findWithGroups(value)
  if (fullMatch != null) {
    val (expression) = fullMatch.destructured
    return try {
      return evaluateExpression(expression, stateHierarchy, operationHandlers, logger)
    } catch (error: Throwable) {
      error.message?.let {
        logger.warn(it)
      }
      null
    }
  }

  try {
    return value.replace(expressionRegex) {
      val (slashes, actualExpression) = it.destructured
      val isExpressionEscaped = slashes.length % 2 == 1
      val escapedSlashes = slashes.replace("""\\""", """\""")

      if (isExpressionEscaped) return@replace "${escapedSlashes.dropLast(1)}@{$actualExpression}"

      val expressionValue = evaluateExpression(actualExpression, stateHierarchy, operationHandlers, logger)
          ?: return@replace escapedSlashes
      return@replace "$escapedSlashes${expressionValue}"
    }
  } catch (error: Throwable) {
    error.message?.let {
      logger.warn(it)
    }
    return value.replace(expressionRegex, "")
  }
}
