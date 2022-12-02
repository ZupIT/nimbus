package br.com.zup.nimbus.core.expression

import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.OperationHandler
import br.com.zup.nimbus.core.dependency.CommonDependency
import br.com.zup.nimbus.core.dependency.Dependent
import br.com.zup.nimbus.core.log.Logger
import br.com.zup.nimbus.core.scope.CloneAfterInitializationError
import br.com.zup.nimbus.core.scope.DoubleInitializationError
import br.com.zup.nimbus.core.scope.LazilyScoped
import br.com.zup.nimbus.core.scope.Scope
import br.com.zup.nimbus.core.scope.closestScopeWithType

class Operation(
  private val handler: OperationHandler,
  private val arguments: List<Expression>,
): Expression, CommonDependency(), Dependent, LazilyScoped<Operation> {
  private var value: Any? = null
  private var hasInitialized = false
  private var getLogger: (() -> Logger?)? = null

  override fun initialize(scope: Scope) {
    if (hasInitialized) throw DoubleInitializationError()
    getLogger = { scope.closestScopeWithType<Nimbus>()?.logger }
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
    val newValue = try {
      handler(argValues)
    } catch (@Suppress("TooGenericExceptionCaught") t: Throwable) {
      getLogger?.let { it()?.error(t.message ?: t.stackTraceToString()) }
      null
    }
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
