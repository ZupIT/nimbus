package br.com.zup.nimbus.core.expression

import br.com.zup.nimbus.core.dependency.CommonDependency
import br.com.zup.nimbus.core.dependency.Dependent
import br.com.zup.nimbus.core.scope.CloneAfterInitializationError
import br.com.zup.nimbus.core.scope.DoubleInitializationError
import br.com.zup.nimbus.core.scope.LazilyScoped
import br.com.zup.nimbus.core.scope.Scope

class StringTemplate(
  private val composition: List<Expression>,
): Expression, CommonDependency(), Dependent, LazilyScoped<StringTemplate> {
  private var value: String = ""
  private var hasInitialized = false

  override fun initialize(scope: Scope) {
    if (hasInitialized) throw DoubleInitializationError()
    composition.forEach {
      if (it is LazilyScoped<*>) it.initialize(scope)
      if (it is CommonDependency) it.addDependent(this)
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
