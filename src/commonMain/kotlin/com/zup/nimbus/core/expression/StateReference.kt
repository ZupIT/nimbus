package com.zup.nimbus.core.expression

import com.zup.nimbus.core.ServerDrivenState
import com.zup.nimbus.core.dependency.Dependency
import com.zup.nimbus.core.dependency.Dependent
import com.zup.nimbus.core.scope.CloneAfterInitializationError
import com.zup.nimbus.core.scope.DoubleInitializationError
import com.zup.nimbus.core.scope.LazilyScoped
import com.zup.nimbus.core.scope.Scope
import com.zup.nimbus.core.utils.valueOfPath

class StateReference(
  private var id: String,
  private val path: String,
  private var onNotFound: ((String, Scope) -> Unit)? = null,
): Expression, Dependency(), Dependent, LazilyScoped<StateReference> {
  private var state: ServerDrivenState? = null
  private var value: Any? = null

  override fun initialize(scope: Scope) {
    if (state != null) throw DoubleInitializationError()
    state = scope.closestState(id)
    if (state == null) onNotFound?.let { it(id, scope) }
    update()
    hasChanged = false
    state?.addDependent(this)
    onNotFound = null
  }

  override fun getValue(): Any? {
    return value
  }

  override fun update() {
    val newValue: Any? = valueOfPath(state?.value, path)
    if (value != newValue) {
      value = newValue
      hasChanged = true
    }
  }

  override fun clone(): StateReference {
    if (state != null) throw CloneAfterInitializationError()
    return StateReference(id, path, onNotFound)
  }
}
