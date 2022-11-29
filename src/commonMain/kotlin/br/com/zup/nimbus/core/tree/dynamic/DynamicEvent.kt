package br.com.zup.nimbus.core.tree.dynamic

import br.com.zup.nimbus.core.dependency.CommonDependency
import br.com.zup.nimbus.core.ActionTriggeredEvent
import br.com.zup.nimbus.core.scope.CloneAfterInitializationError
import br.com.zup.nimbus.core.scope.DoubleInitializationError
import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.ServerDrivenState
import br.com.zup.nimbus.core.ServerDrivenView
import br.com.zup.nimbus.core.dependency.Dependency
import br.com.zup.nimbus.core.dependency.DependencyUpdateManager
import br.com.zup.nimbus.core.dependency.UpdateError
import br.com.zup.nimbus.core.scope.CommonScope
import br.com.zup.nimbus.core.scope.LazilyScoped
import br.com.zup.nimbus.core.scope.Scope
import br.com.zup.nimbus.core.scope.closestScopeWithType
import br.com.zup.nimbus.core.tree.ServerDrivenEvent
import br.com.zup.nimbus.core.tree.ServerDrivenNode

/**
 * DynamicEvents are a type of ServerDrivenEvent that can run DynamicActions.
 */
@Suppress("UseCheckOrError")
class DynamicEvent(
  override val name: String,
): ServerDrivenEvent, LazilyScoped<DynamicEvent>, CommonScope(listOf(ServerDrivenState(name, null))) {
  override lateinit var actions: List<DynamicAction>

  override val node: ServerDrivenNode by lazy {
    closestScopeWithType() ?: throw IllegalStateException("This event is not linked to a node!")
  }
  override val view: ServerDrivenView by lazy {
    closestScopeWithType() ?: throw IllegalStateException("This event is not linked to a view!")
  }
  override val nimbus: Nimbus by lazy {
    closestScopeWithType() ?: throw IllegalStateException("This event is not linked to a nimbus instance!")
  }

  private fun update(dependencies: Set<Dependency>) {
    try {
      DependencyUpdateManager.updateDependentsOf(dependencies)
    } catch(e: UpdateError) {
      nimbus.logger.error(e.message)
    }
  }

  override fun run() {
    val dependencies = mutableSetOf<CommonDependency>()
    actions.forEach {
      try {
        it.handler(ActionTriggeredEvent(action = it, dependencies = dependencies, scope = this))
      } catch (@Suppress("TooGenericExceptionCaught") t: Throwable) {
        nimbus.logger.error(t.message ?: "Unknown error")
      }
    }
    update(dependencies)
  }

  override fun run(implicitStateValue: Any?) {
    states!!.first().set(implicitStateValue)
    update(states.toSet())
    run()
  }

  override fun initialize(scope: Scope) {
    if (parent != null) throw DoubleInitializationError()
    parent = scope
    actions.forEach { it.initialize(this) }
  }

  override fun clone(): DynamicEvent {
    if (parent != null) throw CloneAfterInitializationError()
    val cloned = DynamicEvent(name)
    cloned.actions = actions.map { it.clone() }
    return cloned
  }
}
