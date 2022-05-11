package com.zup.nimbus.core.render

import com.zup.nimbus.core.tree.ServerDrivenState
import com.zup.nimbus.core.utils.then
import io.ktor.util.valuesOf
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class ExpressionResolver {
  companion object Factory {
    val expressionRegex = """/(\\*)@\{(([^'\}]|('([^'\\]|\\.)*'))*)\}/g""".toRegex()
    val fullMatchExpressionRegex = """^@\{(([^'\}]|('([^'\\]|\\.)*'))*)\}$""".toRegex()

    private fun parseParameters(parameterString: String): List<String> {
      val transitions: Map<String, List<Transition>> = mapOf(
        "initial" to listOf(
          Transition("""/,|$/""".toRegex(), null, null, "final"), // end of parameter
          Transition("(", "(", null, "insideParameterList"), // start of a parameter list
          Transition("""/'([^']|(\\.))*'/"""".toRegex(), null, null, "initial"), // strings
          Transition("""/[^\)]/"""".toRegex(), null, null, "initial"), // general symbols
        ),
        "insideParameterList" to listOf(
          Transition("(", "(", null, "insideParameterList"), // start of another parameter list
          Transition(")", null, "(", "isParameterListOver"), // end of a parameter list, check if still inside a parameter list
          Transition("""/'([^']|(\\.))*'/"""".toRegex(), null, null, "insideParameterList"), // strings
          Transition("""/./""".toRegex(), null, null, "insideParameterList"), // general symbols
        ),
        "isParameterListOver" to listOf(
          Transition(null, null, Automaton.empty, "initial"), // end of parameter list, go back to initial state
          Transition(null, null, null, "insideParameterList"), // still inside a parameter list, go back to state "insideParameterList"
        ),
      )

      val params = DPAParams("initial", "final", transitions)

      val dpa = Automaton.createDPA(params)
      val parameters: MutableList<String> = mutableListOf()
      var position = 0

      while (position < parameterString.length) {
        val match = dpa.match(parameterString.substring(position))
            ?: throw Error("wrong format for parameters: $parameterString")
        parameters.add(match.replace("""/,$/""".toRegex(), "").trim())
        position += match.length
      }

      return parameters
    }

    private fun getContextBindingValue(path: String, stateHierarchy: List<ServerDrivenState>?): Any? {
      if (!path.matches("""/^[\w\d_]+(\[\d+\])*(\.([\w\d_]+(\[\d+\])*))*$/""".toRegex())) {
        throw Error("invalid path \"$path\". Please, make sure your variable names contain only letters, numbers and the symbol \"_\". To access substructures use \".\" and to access array indexes use \"[index]\".")
      }

      val pathMatch = Regex("""/^([^\.\[\]]+)\.?(.*)/""").find(path) ?: return null
      val (stateId, statePath) = pathMatch.destructured
      val state = stateHierarchy?.find { it.id == stateId } ?: throw Error("Couldn't find context with id \"$stateId\"")

      // [statePath] - arthur verify later
      // return ((statePath.isNotEmpty() && statePath.isNotBlank()) then state ?: state.value
      valuesOf()
      return (((statePath.isNotEmpty() && statePath.isNotBlank()) then state) ?: "")
    }

    private fun getLiteralValue(literal: String): Any? {
      // true, false or null
      if (literal == "true") return true
      if (literal == "false") return false
      if (literal == "null") return null

      // number
      if (literal.matches("""/^\d+(\.\d+)?$/""".toRegex())) return literal.toFloat()

      // string
      if (literal.startsWith("""\""") && literal.endsWith("""\""")) {
        return literal
          .replace("""/(^')|('$)/g""".toRegex(), "")
          .replace("""/\\'/g""".toRegex(), """\""")
      }

      return null
    }

    private fun getOperationValue(operation: String, stateHierarchy: List<ServerDrivenState>?): Any? {
      val match = Regex("""/^(\w+)\((.*)\)$/""").find(operation)
        ?: throw Error("invalid operation in expression: $operation")

      val (operationName, paramString) = match.destructured
      // if (operationHandlers[operationName]) {
      //  throw Error("operation with name \"$operationName\" doesn't exist.")
      // }

      val params = parseParameters(paramString)
      val resolvedParams = params.map { param -> evaluateExpression(param, stateHierarchy) }

      // val fn = operationHandlers[operationName] as Operation
      // return fn(...resolvedParams)

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
  return value.matches(ExpressionResolver.expressionRegex)
}

fun resolveExpressions(value: String, stateHierarchy: List<ServerDrivenState>?): Any? {
  val fullMatch = ExpressionResolver.fullMatchExpressionRegex.find(value)
  if (fullMatch != null) {
    val (expression) = fullMatch.destructured
    return try {
      val bindingValue = ExpressionResolver.evaluateExpression(expression, stateHierarchy)
      ((bindingValue == null) then value) ?: bindingValue
    } catch (error: Throwable) {
      // logger.warn(error)
      null
    }
  }
  return value.replace(ExpressionResolver.expressionRegex) { matchResult ->
    val (bindingStr, slashes, path) = matchResult.destructured
    val isBindingEscaped = slashes.length % 2 == 1
    val escapedSlashes = slashes.replace("""/\\\\/g""".toRegex(), "\\")

    if (isBindingEscaped) "${escapedSlashes.replace("""/\\$/""".toRegex(), "")}@{$path}"

    var bindingValue: Any? = null
    try {
      bindingValue = ExpressionResolver.evaluateExpression(path, stateHierarchy)
    } catch (error: Throwable) {
      // logger.warn(error)
    }

    val asString = when (bindingValue) {
      is String -> bindingValue
      is Int -> bindingValue.toString()
      is Float -> bindingValue.toString()
      is Boolean -> bindingValue.toString()
      else -> Json.encodeToString(bindingValue)
    }

    ((bindingValue == null) then escapedSlashes) ?: "$escapedSlashes$asString"
  }
}
