package com.zup.nimbus.core.tree.container

import com.zup.nimbus.core.scope.CloneAfterInitializationError
import com.zup.nimbus.core.scope.DoubleInitializationError
import com.zup.nimbus.core.scope.LazilyScoped
import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.expression.Expression
import com.zup.nimbus.core.dependency.CommonDependency
import com.zup.nimbus.core.dependency.Dependent
import com.zup.nimbus.core.scope.Scope
import com.zup.nimbus.core.tree.DynamicEvent
import com.zup.nimbus.core.tree.ServerDrivenEvent

class PropertyContainer private constructor(
  private val nimbus: Nimbus,
): LazilyScoped<PropertyContainer>, CommonDependency(), Dependent {
  // General variables

  private var expressionEvaluators = mutableListOf<() -> Unit>()
  private lateinit var currentProperties: Map<String, Any?>
  private var hasInitialized = false
  private var disabled = false

  // Variables that should be freed upon initialization

  private var expressions: MutableList<Expression>? = mutableListOf()
  private var events: MutableList<DynamicEvent>? = mutableListOf()

  // Constructors

  constructor(properties: Map<String, Any?>, nimbus: Nimbus): this(nimbus) {
    currentProperties = parseMap(properties)
  }

  private constructor(
    currentProperties: Map<String, Any?>,
    expressions: MutableList<Expression>,
    expressionEvaluators: MutableList<() -> Unit>,
    events: MutableList<DynamicEvent>,
    nimbus: Nimbus,
  ): this(nimbus) {
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
      if (it is CommonDependency) it.dependents.add(this)
    }
    events?.forEach { it.initialize(scope) }
    expressions = null
    events = null
    hasInitialized = true
    update()
    hasChanged = false
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
    return PropertyContainer(clonedProperties, clonedExpressions, clonedExpressionEvaluators, clonedEvents, nimbus)
  }

  // Other methods

  private fun parseAny(toParse: Any?, key: String? = null): Any? {
    return when(toParse) {
      is String -> {
        if (nimbus.expressionParser.containsExpression(toParse)) {
          val expression = nimbus.expressionParser.parseString(toParse)
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

  private fun parseMap(toParse: Map<String, Any?>): HashMap<String, Any?> {
    val result = HashMap<String, Any?>()
    toParse.forEach {
      val key = it.key
      val value = it.value
      val parsed = parseAny(value, key)
      if (parsed is Expression) {
        expressionEvaluators.add { result[key] = parsed.getValue() }
      }
      result[key] = parsed
    }
    return result
  }

  fun read(): Map<String, Any?> {
    return currentProperties
  }

  override fun update() {
    if (disabled) return
    expressionEvaluators.forEach { it() }
    hasChanged = true
  }
}
