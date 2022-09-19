package com.zup.nimbus.core.tree.container

import com.zup.nimbus.core.scope.LazilyScoped
import com.zup.nimbus.core.expression.Expression
import com.zup.nimbus.core.expression.Literal
import com.zup.nimbus.core.tree.DynamicEvent

internal object PropertyCopying {
  fun copyMap(
    source: Map<String, Any?>,
    expressions: MutableList<Expression>,
    expressionEvaluators: MutableList<() -> Unit>,
    events: MutableList<DynamicEvent>,
  ): MutableMap<String, Any?> {
    val result = mutableMapOf<String, Any?>()
    source.forEach { entry ->
      result[entry.key] = copyAny(
        source = entry.value,
        setter = { result[entry.key] = it },
        expressions,
        expressionEvaluators,
        events,
      )
    }
    return result
  }

  private fun copyList(
    source: List<Any?>,
    expressions: MutableList<Expression>,
    expressionEvaluators: MutableList<() -> Unit>,
    events: MutableList<DynamicEvent>,
  ): MutableList<Any?> {
    val result = mutableListOf<Any?>()
    source.forEachIndexed { index, value ->
      result.add(copyAny(
        source = value,
        setter = { result[index] = it },
        expressions,
        expressionEvaluators,
        events,
      ))
    }
    return result
  }

  private fun copyAny(
    source: Any?,
    setter: (value: Any?) -> Unit,
    expressions: MutableList<Expression>,
    expressionEvaluators: MutableList<() -> Unit>,
    events: MutableList<DynamicEvent>,
  ): Any? {
    return when(source) {
      is String, is Number, is Boolean, is Literal -> source
      is List<*> -> copyList(source, expressions, expressionEvaluators, events)
      is Map<*, *> -> {
        @Suppress("UNCHECKED_CAST")
        copyMap(source as Map<String, Any?>, expressions, expressionEvaluators, events)
      }
      is Expression -> {
        val clonedExpression = if (source is LazilyScoped<*>) source.clone() as Expression else source
        expressions.add(clonedExpression)
        expressionEvaluators.add { setter(clonedExpression.getValue()) }
        clonedExpression
      }
      is DynamicEvent -> {
        val clonedEvent = source.clone()
        events.add(clonedEvent)
        clonedEvent
      }
      null -> null
      else -> throw IllegalArgumentException(
        "Unsupported value type while trying to copy property map: ${source::class.qualifiedName}"
      )
    }
  }
}
