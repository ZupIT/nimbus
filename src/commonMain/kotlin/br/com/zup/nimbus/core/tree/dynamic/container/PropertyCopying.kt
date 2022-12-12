/*
 * Copyright 2023 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.zup.nimbus.core.tree.dynamic.container

import br.com.zup.nimbus.core.scope.LazilyScoped
import br.com.zup.nimbus.core.expression.Expression
import br.com.zup.nimbus.core.expression.Literal
import br.com.zup.nimbus.core.tree.dynamic.DynamicEvent

/**
 * This is a helper for the PropertyContainer class. Its objective is to help cloning the parsed properties into a new
 * PropertyContainer.
 */
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
      result.add(
        copyAny(
        source = value,
        setter = { result[index] = it },
        expressions,
        expressionEvaluators,
        events,
      )
      )
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
        (copyMap(
        source as Map<String, Any?>,
        expressions,
        expressionEvaluators,
        events
    ))
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
