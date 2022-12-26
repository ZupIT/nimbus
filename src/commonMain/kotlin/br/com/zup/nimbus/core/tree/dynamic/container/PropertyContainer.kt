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

import br.com.zup.nimbus.core.scope.CloneAfterInitializationError
import br.com.zup.nimbus.core.scope.DoubleInitializationError
import br.com.zup.nimbus.core.scope.LazilyScoped
import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.expression.Expression
import br.com.zup.nimbus.core.dependency.CommonDependency
import br.com.zup.nimbus.core.dependency.DependencyUpdateManager
import br.com.zup.nimbus.core.dependency.Dependent
import br.com.zup.nimbus.core.expression.StateReference
import br.com.zup.nimbus.core.scope.Scope
import br.com.zup.nimbus.core.tree.dynamic.DynamicEvent

/**
 * Manages a dynamic property map, where a value can change depending on the current evaluation of an expression.
 */
class PropertyContainer private constructor(
  private val nimbus: Nimbus,
  private val detached: Boolean,
): LazilyScoped<PropertyContainer>, CommonDependency(), Dependent {
  // General variables

  /**
   * Functions that must be called to update the current map of properties. Each function in this list updates a
   * specific key in the map according to its original expression.
   */
  private var expressionEvaluators = mutableListOf<() -> Unit>()
  /**
   * The current processed map of properties. This map must never contain an expression, since it will be received by
   * the UI Layer.
   */
  private lateinit var currentProperties: Map<String, Any?>
  private var hasInitialized = false

  // Variables that should be freed upon initialization

  private var expressions: MutableList<Expression>? = mutableListOf()
  private var events: MutableList<DynamicEvent>? = mutableListOf()

  // Constructors

  constructor(properties: Map<String, Any?>, nimbus: Nimbus, detached: Boolean = false): this(nimbus, detached) {
    currentProperties = parseMap(properties)
  }

  private constructor(
    currentProperties: Map<String, Any?>,
    expressions: MutableList<Expression>,
    expressionEvaluators: MutableList<() -> Unit>,
    events: MutableList<DynamicEvent>,
    nimbus: Nimbus,
    detached: Boolean,
  ): this(nimbus, detached) {
    this.currentProperties = currentProperties
    this.expressions = expressions
    this.expressionEvaluators = expressionEvaluators
    this.events = events
  }

  // Lazy initialization and cloning

  override fun initialize(scope: Scope) {
    if (hasInitialized) throw DoubleInitializationError()
    expressions?.forEach {
      if (it is LazilyScoped<*>) it.initialize(scope)
      if (!detached && it is CommonDependency) it.dependents.add(this)
    }
    events?.forEach { it.initialize(scope) }
    expressions = null
    events = null
    hasInitialized = true
    if (!detached) {
      update()
      hasChanged = false
    }
  }

  override fun clone(): PropertyContainer {
    if (hasInitialized) throw CloneAfterInitializationError()
    val clonedExpressions = mutableListOf<Expression>()
    val clonedEvents = mutableListOf<DynamicEvent>()
    val clonedExpressionEvaluators = mutableListOf<() -> Unit>()
    val clonedProperties = PropertyCopying.copyMap(
      source = currentProperties,
      clonedExpressions,
      clonedExpressionEvaluators,
      clonedEvents,
    )
    return PropertyContainer(
      clonedProperties, clonedExpressions, clonedExpressionEvaluators, clonedEvents, nimbus, detached
    )
  }

  // Other methods

  /**
   * Parses any value in the original property map.
   */
  private fun parseAny(toParse: Any?, key: String? = null): Any? {
    return when(toParse) {
      is String -> {
        if (nimbus.expressionParser.containsExpression(toParse)) {
          val expression = nimbus.expressionParser.parseString(toParse, detached)
          expressions?.add(expression)
          expression
        }
        else toParse
      }
      is List<*> -> {
        if (key != null && nimbus.eventBuilder.isJsonEvent(toParse)) {
          val event = nimbus.eventBuilder.buildFromJsonMap(key, toParse)
          events?.add(event)
          event
        } else {
          parseList(toParse)
        }
      }
      is Map<*, *> -> @Suppress("UNCHECKED_CAST") parseMap(toParse as Map<String, Any?>)
      else -> toParse
    }
  }

  /**
   * Parses a list in the original property map.
   */
  private fun parseList(toParse: List<Any?>): List<Any?> {
    val result = mutableListOf<Any?>()
    toParse.forEachIndexed { index, value ->
      val parsed = parseAny(value)
      if (parsed is Expression) {
        expressionEvaluators.add { result[index] = parsed.getValue() }
      }
      result.add(index, parsed)
    }
    return result
  }

  /**
   * Parses a map in the original property map.
   */
  private fun parseMap(toParse: Map<String, Any?>): HashMap<String, Any?> {
    val result = HashMap<String, Any?>()
    toParse.forEach {
      val key = it.key
      val value = it.value
      val parsed = parseAny(value, key)
      if (parsed is Expression) {
        expressionEvaluators.add {
          if (detached && parsed is Dependent) parsed.update()
          result[key] = parsed.getValue()
        }
      }
      result[key] = parsed
    }
    return result
  }

  /**
   * Returns the current map of properties with each value updated according to the most recent result of its expression
   * (if it was originally an expression).
   */
  fun read(): Map<String, Any?> {
    return currentProperties
  }

  override fun update() {
    expressionEvaluators.forEach { it() }
    hasChanged = true
  }
}
