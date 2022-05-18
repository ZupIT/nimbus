package com.zup.nimbus.core.render

import com.zup.nimbus.core.OperationHandler
import com.zup.nimbus.core.log.Logger
import com.zup.nimbus.core.tree.ServerDrivenState
import com.zup.nimbus.core.utils.then
import com.zup.nimbus.core.utils.valueOf
import kotlin.reflect.typeOf
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

internal class ExpressionResolver {
  companion object Factory {
    val expressionRegex = """(\\*)@\{(([^'\}]|('([^'\\]|\\.)*'))*)\}""".toRegex()
    val fullMatchExpressionRegex = """^@\{(([^'\}]|('([^'\\]|\\.)*'))*)\}$""".toRegex()

    private fun parseParameters(parameterString: String): List<String> {
      val transitions: Map<String, List<Transition>> = mapOf(
        "initial" to listOf(
          Transition(""",|$""".toRegex(), null, null, "final"), // end of parameter
          Transition("(", "(", null, "insideParameterList"), // start of a parameter list
          Transition("""'([^']|(\\.))*'""".toRegex(), null, null, "initial"), // strings
          Transition("""[^\)]""".toRegex(), null, null, "initial"), // general symbols
        ),
        "insideParameterList" to listOf(
          Transition("(", "(", null, "insideParameterList"), // start of another parameter list
          Transition(")", null, "(", "isParameterListOver"), // end of a parameter list, check if still inside a parameter list
          Transition("""'([^']|(\\.))*'""".toRegex(), null, null, "insideParameterList"), // strings
          Transition(".".toRegex(), null, null, "insideParameterList"), // general symbols
        ),
        "isParameterListOver" to listOf(
          Transition(null, EMPTY, "initial"), // end of parameter list, go back to initial state
          Transition(null, null, "insideParameterList"), // still inside a parameter list, go back to state "insideParameterList"
        ),
      )

      val dpa = DPA("initial", "final", transitions)
      val parameters: MutableList<String> = mutableListOf()
      var position = 0

      while (position < parameterString.length) {
        val match = dpa.match(parameterString.substring(position))
            ?: throw Error("wrong format for parameters: $parameterString")
        parameters.add(match.replace(",$".toRegex(), "").trim())
        position += match.length
      }

      return parameters
    }

    private fun getStateValue(path: String, stateHierarchy: List<ServerDrivenState>?, logger: Logger?): Any? {
      if (!path.matches("""^[\w\d_]+(\[\d+\])*(\.([\w\d_]+(\[\d+\])*))*$""".toRegex())) {
        throw Error("invalid path \"$path\". Please, make sure your variable names contain only letters, numbers and the symbol \"_\". To access substructures use \".\" and to access array indexes use \"[index]\".")
      }

      val pathMatch = Regex("""^([^\.\[\]]+)\.?(.*)""").find(path) ?: return null
      val (stateId, statePath) = pathMatch.destructured
      val state = stateHierarchy?.find { it.id == stateId } ?: throw Error("Couldn't find context with id \"$stateId\"")
      if (statePath.isNotEmpty() && statePath.isNotBlank()) {
        return try {
          valueOf(state.value, statePath)
        } catch (error: Throwable) {
          error.message?.let {
            logger?.warn(it)
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

      if (literal.matches("""^\d+(\.\d+)?$""".toRegex())) return literal.toFloat()

      if (literal.startsWith("'") && literal.endsWith("'")) {
        return literal
          .replace("""(^')|('$)""".toRegex(), "")
          .replace("\\'", "'")
      }

      return null
    }

    private fun getOperationValue(
      operation: String,
      stateHierarchy: List<ServerDrivenState>?,
      operationHandlers: Map<String, OperationHandler>?,
      logger: Logger?,
    ): Any? {
      val match = """^(\w+)\((.*)\)$""".toRegex().find(operation)
        ?: throw Error("invalid operation in expression: $operation")

      val (operationName, paramString) = match.destructured
      if (operationHandlers == null || operationHandlers[operationName] == null) {
        throw Error("operation with name \"$operationName\" doesn't exist.")
      }

      val params = parseParameters(paramString)
      val resolvedParams = params.map { param -> evaluateExpression(param, stateHierarchy, operationHandlers, logger) }

      val fn = operationHandlers[operationName]
      if (fn != null) {
        return fn(resolvedParams as List<Any>)
      }
      return null
    }

    fun evaluateExpression(
      expression: String,
      stateHierarchy: List<ServerDrivenState>?,
      operationHandlers: Map<String, OperationHandler>?,
      logger: Logger?,
    ): Any? {
      val literalValue = getLiteralValue(expression)
      if (literalValue != null) return literalValue

      val isOperation = expression.contains("(")
      if (isOperation) return getOperationValue(expression, stateHierarchy, operationHandlers, logger)

      return getStateValue(expression, stateHierarchy, logger)
    }
  }
}

fun containsExpression(value: String): Boolean {
  return ExpressionResolver.expressionRegex.containsMatchIn(value)
}

fun resolveExpressions(
  value: String,
  stateHierarchy: List<ServerDrivenState>?,
  operationHandlers: Map<String, OperationHandler>?,
  logger: Logger?,
): Any? {
  val fullMatch = ExpressionResolver.fullMatchExpressionRegex.find(value)
  if (fullMatch != null) {
    val (expression) = fullMatch.destructured
    return try {
      val expressionValue = ExpressionResolver.evaluateExpression(expression, stateHierarchy, operationHandlers, logger)
      ((expressionValue == null) then value) ?: expressionValue
    } catch (error: Throwable) {
      error.message?.let {
        logger?.warn(it)
      }
      null
    }
  }

  try {
    return value.replace(ExpressionResolver.expressionRegex) {
      val (slashes, path) = it.destructured
      val isExpressionEscaped = slashes.length % 2 == 1
      val escapedSlashes = slashes.replace("\\\\", "\\")

      if (isExpressionEscaped) return@replace "${escapedSlashes.replace("\\\\$".toRegex(), "")}@{$path}"

      val expressionValue = ExpressionResolver.evaluateExpression(path, stateHierarchy, operationHandlers, logger)
          ?: return@replace escapedSlashes
      return@replace "$escapedSlashes${expressionValue}"
    }
  } catch (error: Throwable) {
    error.message?.let {
      logger?.warn(it)
    }
    return value.replace(ExpressionResolver.expressionRegex, "")
  }
}
