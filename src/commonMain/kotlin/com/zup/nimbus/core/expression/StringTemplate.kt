package com.zup.nimbus.core.expression

import com.zup.nimbus.core.dependency.Dependency
import com.zup.nimbus.core.dependency.Dependent
import com.zup.nimbus.core.scope.CloneAfterInitializationError
import com.zup.nimbus.core.scope.DoubleInitializationError
import com.zup.nimbus.core.scope.LazilyScoped
import com.zup.nimbus.core.scope.Scope

class StringTemplate(
  private val composition: List<Expression>,
): Expression, Dependency(), Dependent, LazilyScoped<StringTemplate> {
  private var value: String = ""
  private var hasInitialized = false

  override fun initialize(scope: Scope) {
    if (hasInitialized) throw DoubleInitializationError()
    composition.forEach {
      if (it is LazilyScoped<*>) it.initialize(scope)
      if (it is Dependency) it.addDependent(this)
    }
    hasInitialized = true
    update()
    hasChanged = false
  }

  override fun getValue(): String {
    return value
  }

  override fun update() {
    val newValue = composition.joinToString("") { "${it.getValue() ?: ""}" }
    if (value != newValue) {
      value = newValue
      hasChanged = true
    }
  }

  override fun clone(): StringTemplate {
    if (hasInitialized) throw CloneAfterInitializationError()
    val clonedComposition = composition.map { if (it is LazilyScoped<*>) it.clone() as Expression else it }
    return StringTemplate(clonedComposition)
  }
}
