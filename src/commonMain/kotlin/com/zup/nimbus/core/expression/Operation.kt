package com.zup.nimbus.core.expression

import com.zup.nimbus.core.OperationHandler
import com.zup.nimbus.core.dependency.CommonDependency
import com.zup.nimbus.core.dependency.Dependent
import com.zup.nimbus.core.scope.CloneAfterInitializationError
import com.zup.nimbus.core.scope.DoubleInitializationError
import com.zup.nimbus.core.scope.LazilyScoped
import com.zup.nimbus.core.scope.Scope

class Operation(
  private val handler: OperationHandler,
  private val arguments: List<Expression>,
): Expression, CommonDependency(), Dependent, LazilyScoped<Operation> {
  private var value: Any? = null
  private var hasInitialized = false

  override fun initialize(scope: Scope) {
    if (hasInitialized) throw DoubleInitializationError()
    arguments.forEach {
      if (it is LazilyScoped<*>) it.initialize(scope)
      if (it is CommonDependency) it.addDependent(this)
    }
    hasInitialized = true
    update()
    hasChanged = false
  }

  override fun update() {
    val argValues = arguments.map { it.getValue() }
    val newValue = handler(argValues)
    if (value != newValue) {
      value = newValue
      hasChanged = true
    }
  }

  override fun getValue(): Any? {
    return value
  }

  override fun clone(): Operation {
    if (hasInitialized) throw CloneAfterInitializationError()
    val clonedArguments = arguments.map { if (it is LazilyScoped<*>) it.clone() as Expression else it }
    return Operation(handler, clonedArguments)
  }
}
