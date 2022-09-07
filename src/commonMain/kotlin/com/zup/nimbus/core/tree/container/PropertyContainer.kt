package com.zup.nimbus.core.tree.container

import com.zup.nimbus.core.ServerDrivenView
import com.zup.nimbus.core.ast.Expression
import com.zup.nimbus.core.ast.ExpressionParser
import com.zup.nimbus.core.dependencyGraph.Dependency
import com.zup.nimbus.core.dependencyGraph.Dependent
import com.zup.nimbus.core.tree.builder.ActionBuilder
import com.zup.nimbus.core.tree.builder.EventBuilder
import com.zup.nimbus.core.tree.stateful.Stateful

class PropertyContainer(
  private val expressionParser: ExpressionParser,
  private val actionBuilder: ActionBuilder,
  private val view: ServerDrivenView,
  properties: Map<String, Any?>,
  stateSource: Stateful,
): Dependency(), Dependent {
  private var expressionEvaluators = mutableListOf<() -> Unit>()
  private var currentProperties = parseMap(properties, stateSource)

  init { update() }

  private fun parseExpression(toParse: String, stateSource: Stateful): Expression {
    val expression = expressionParser.parse(toParse, stateSource)
    if (expression is Dependency) expression.dependents.add(this)
    return expression
  }

  private fun parseAny(toParse: Any?, stateSource: Stateful, key: String? = null): Any? {
    return when(toParse) {
      is String -> {
        if (expressionParser.containsExpression(toParse)) parseExpression(toParse, stateSource)
        else toParse
      }
      is List<*> -> {
        if (key != null && EventBuilder.isJsonEvent(toParse)) {
          EventBuilder.buildFromJsonEvent(key, toParse, stateSource, view, actionBuilder)
        } else {
          parseList(toParse, stateSource)
        }
      }
      is Map<*, *> -> parseMap(toParse as Map<String, Any?>, stateSource)
      else -> toParse
    }
  }

  private fun parseList(toParse: List<Any?>, stateSource: Stateful): List<Any?> {
    val result = mutableListOf<Any?>()
    toParse.forEachIndexed { index, value ->
      val parsed = parseAny(value, stateSource)
      if (parsed is Expression) {
        expressionEvaluators.add { result[index] = parsed.getValue() }
        result[index] = value
      } else {
        result[index] = parsed
      }
    }
    return result
  }

  private fun parseMap(toParse: Map<String, Any?>, stateSource: Stateful): HashMap<String, Any?> {
    val result = HashMap<String, Any?>()
    toParse.forEach {
      val key = it.key
      val value = it.value
      val parsed = parseAny(value, stateSource, key)
      if (parsed is Expression) {
        expressionEvaluators.add { result[key] = parsed.getValue() }
        result[key] = value
      } else {
        result[key] = parsed
      }
    }
    return result
  }

  fun read(): Map<String, Any?> {
    return currentProperties
  }

  override fun update() {
    expressionEvaluators.forEach { it() }
  }
}
