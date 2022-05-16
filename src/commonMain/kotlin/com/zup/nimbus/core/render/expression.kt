package com.zup.nimbus.core.render

import com.zup.nimbus.core.tree.ServerDrivenState
import com.zup.nimbus.core.utils.then
import com.zup.nimbus.core.utils.valueOf
import kotlin.reflect.typeOf
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

internal class ExpressionResolver {
  companion object Factory {
    val expressionRegex = "(\\\\*)@\\{(([^'\\}]|('([^'\\\\]|\\\\.)*'))*)\\}".toRegex()
    val fullMatchExpressionRegex = "^@\\{(([^'\\}]|('([^'\\\\]|\\\\.)*'))*)\\}$".toRegex()

    private val operationHandlers = emptyMap<String, (params: List<Any?>) -> Any?>()

    private fun parseParameters(parameterString: String): List<String> {
      val transitions: Map<String, List<Transition>> = mapOf(
        "initial" to listOf(
          Transition(",|$".toRegex(), null, null, "final"), // end of parameter
          Transition("(", "(", null, "insideParameterList"), // start of a parameter list
          Transition("'([^']|(\\\\.))*'".toRegex(), null, null, "initial"), // strings
          Transition("[^\\)]".toRegex(), null, null, "initial"), // general symbols
        ),
        "insideParameterList" to listOf(
          Transition("(", "(", null, "insideParameterList"), // start of another parameter list
          Transition(")", null, "(", "isParameterListOver"), // end of a parameter list, check if still inside a parameter list
          Transition("'([^']|(\\\\.))*'".toRegex(), null, null, "insideParameterList"), // strings
          Transition(".".toRegex(), null, null, "insideParameterList"), // general symbols
        ),
        "isParameterListOver" to listOf(
          Transition(null, null, Automaton.empty, "initial"), // end of parameter list, go back to initial state
          Transition(null, null, null, "insideParameterList"), // still inside a parameter list, go back to state "insideParameterList"
        ),
      )

      val dpaParams = DPAParams("initial", "final", transitions)
      val dpa = Automaton.createDPA(dpaParams)
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

    private fun getContextBindingValue(path: String, stateHierarchy: List<ServerDrivenState>?): Any? {
      if (!path.matches("^[\\w\\d_]+(\\[\\d+\\])*(\\.([\\w\\d_]+(\\[\\d+\\])*))*$".toRegex())) {
        throw Error("invalid path \"$path\". Please, make sure your variable names contain only letters, numbers and the symbol \"_\". To access substructures use \".\" and to access array indexes use \"[index]\".")
      }

      val pathMatch = Regex("^([^\\.\\[\\]]+)\\.?(.*)").find(path) ?: return null
      val (stateId, statePath) = pathMatch.destructured
      val state = stateHierarchy?.find { it.id == stateId } ?: throw Error("Couldn't find context with id \"$stateId\"")
      if (statePath.isNotEmpty() && statePath.isNotBlank()) {
        return try {
          valueOf(state.value, statePath)
        } catch (error: Throwable) {
          // log warn
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

      if (literal.matches("^\\d+(\\.\\d+)?$".toRegex())) return literal.toFloat()

      if (literal.startsWith("'") && literal.endsWith("'")) {
        return literal
          .replace("(^')|('$)".toRegex(), "")
          .replace("\\'", "'")
      }

      return null
    }

    private fun getOperationValue(operation: String, stateHierarchy: List<ServerDrivenState>?): Any? {
      val match = Regex("^(\\w+)\\((.*)\\)$").find(operation)
        ?: throw Error("invalid operation in expression: $operation")

      val (operationName, paramString) = match.destructured
      if (operationHandlers[operationName] != null) {
        throw Error("operation with name \"$operationName\" doesn't exist.")
      }

      val params = parseParameters(paramString)
      val resolvedParams = params.map { param -> evaluateExpression(param, stateHierarchy) }

      val fn = operationHandlers[operationName]
      if (fn != null) {
        return fn(resolvedParams)
      }
      return null
    }

    fun evaluateExpression(expression: String, stateHierarchy: List<ServerDrivenState>?): Any? {
      val literalValue = getLiteralValue(expression)
      if (literalValue != null) return literalValue

      val isOperation = expression.contains("(")
      if (isOperation) return getOperationValue(expression, stateHierarchy)

      return getContextBindingValue(expression, stateHierarchy)
    }
  }
}

fun containsExpression(value: String): Boolean {
  return ExpressionResolver.expressionRegex.containsMatchIn(value)
}

fun resolveExpressions(value: String, stateHierarchy: List<ServerDrivenState>?): Any? {
  val fullMatch = ExpressionResolver.fullMatchExpressionRegex.find(value)
  if (fullMatch != null) {
    val (expression) = fullMatch.destructured
    return try {
      val bindingValue = ExpressionResolver.evaluateExpression(expression, stateHierarchy)
      ((bindingValue == null) then value) ?: bindingValue
    } catch (error: Throwable) {
      // log warn
      null
    }
  }

  try {
    return value.replace(ExpressionResolver.expressionRegex) {
      val (slashes, path) = it.destructured
      val isBindingEscaped = slashes.length % 2 == 1
      val escapedSlashes = slashes.replace("\\\\", "\\")

      if (isBindingEscaped) return@replace "${escapedSlashes.replace(Regex("\\\\$"), "")}@{$path}"

      val bindingValue = ExpressionResolver.evaluateExpression(path, stateHierarchy)
      if (bindingValue == null) {
        return@replace escapedSlashes
      } else {
        return@replace "$escapedSlashes${bindingValue}"
      }
    }
  } catch (error: Throwable) {
    // log something here
    return value.replace(ExpressionResolver.expressionRegex, "")
  }
}
