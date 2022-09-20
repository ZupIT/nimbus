package com.zup.nimbus.core.tree.dynamic

import com.zup.nimbus.core.dependency.CommonDependency
import com.zup.nimbus.core.ActionTriggeredEvent
import com.zup.nimbus.core.scope.CloneAfterInitializationError
import com.zup.nimbus.core.scope.DoubleInitializationError
import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.ServerDrivenState
import com.zup.nimbus.core.ServerDrivenView
import com.zup.nimbus.core.dependency.DependencyUpdateManager
import com.zup.nimbus.core.scope.CommonScope
import com.zup.nimbus.core.scope.LazilyScoped
import com.zup.nimbus.core.scope.Scope
import com.zup.nimbus.core.scope.closestScopeWithType
import com.zup.nimbus.core.tree.ServerDrivenEvent
import com.zup.nimbus.core.tree.ServerDrivenNode

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

  override fun run() {
    val dependencies = mutableSetOf<CommonDependency>()
    actions.forEach { it.handler(ActionTriggeredEvent(action = it, dependencies = dependencies, scope = this)) }
    DependencyUpdateManager.updateDependentsOf(dependencies)
  }

  override fun run(implicitStateValue: Any?) {
    states!!.first().set(implicitStateValue)
    DependencyUpdateManager.updateDependentsOf(states.toSet())
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
