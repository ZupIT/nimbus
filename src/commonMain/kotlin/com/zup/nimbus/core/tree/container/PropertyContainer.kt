package com.zup.nimbus.core.tree.container

import com.zup.nimbus.core.expression.Expression
import com.zup.nimbus.core.dependencyGraph.Dependency
import com.zup.nimbus.core.dependencyGraph.Dependent
import com.zup.nimbus.core.scope.ViewScope
import com.zup.nimbus.core.tree.builder.EventBuilder
import com.zup.nimbus.core.tree.stateful.Stateful

class PropertyContainer(
  properties: Map<String, Any?>,
  stateSource: Stateful,
  private val scope: ViewScope,
): Dependency(), Dependent {
  private var expressionEvaluators = mutableListOf<() -> Unit>()
  private var currentProperties = parseMap(properties, stateSource)

  init {
    update()
    hasChanged = false
  }

  private fun parseExpression(toParse: String, stateSource: Stateful): Expression {
    val expression = scope.getExpressionParser().parseString(toParse, stateSource)
    if (expression is Dependency) expression.dependents.add(this)
    return expression
  }

  private fun parseAny(toParse: Any?, stateSource: Stateful, key: String? = null): Any? {
    return when(toParse) {
      is String -> {
        if (scope.getExpressionParser().containsExpression(toParse)) parseExpression(toParse, stateSource)
        else toParse
      }
      is List<*> -> {
        if (key != null && EventBuilder.isJsonEvent(toParse)) {
          EventBuilder.buildFromJsonEvent(key, toParse, stateSource, scope)
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
        result.add(index, value)
      } else {
        result.add(index, parsed)
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
    hasChanged = true
  }
}
