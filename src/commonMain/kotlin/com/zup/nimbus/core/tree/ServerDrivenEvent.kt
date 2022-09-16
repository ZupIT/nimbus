package com.zup.nimbus.core.tree

import com.zup.nimbus.core.dependency.Dependency
import com.zup.nimbus.core.dependency.updateDependentsOf
import com.zup.nimbus.core.ActionTriggeredEvent
import com.zup.nimbus.core.scope.CloneAfterInitializationError
import com.zup.nimbus.core.scope.DoubleInitializationError
import com.zup.nimbus.core.scope.LazilyScoped
import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.ServerDrivenState
import com.zup.nimbus.core.ServerDrivenView
import com.zup.nimbus.core.scope.CommonScope
import com.zup.nimbus.core.scope.Scope
import com.zup.nimbus.core.scope.getParentScopeWithType
import com.zup.nimbus.core.tree.node.ServerDrivenNode

class ServerDrivenEvent(
  val name: String,
): LazilyScoped<ServerDrivenEvent>, CommonScope(listOf(ServerDrivenState(name, null))) {
  internal lateinit var actions: List<ServerDrivenAction>

  val node: ServerDrivenNode by lazy {
    getParentScopeWithType() ?: throw IllegalStateException("This event is not linked to a node!")
  }
  val view: ServerDrivenView by lazy {
    getParentScopeWithType() ?: throw IllegalStateException("This event is not linked to a view!")
  }
  val nimbus: Nimbus by lazy {
    view.nimbus
  }

  fun run() {
    val dependencies = mutableSetOf<Dependency>()
    actions.forEach { it.handler(ActionTriggeredEvent(action = it, dependencies = dependencies, scope = this)) }
    updateDependentsOf(dependencies)
  }

  fun run(implicitStateValue: Any?) {
    states!!.first().set(implicitStateValue)
    updateDependentsOf(states.toSet())
    run()
  }

  override fun initialize(scope: Scope) {
    if (parent != null) throw DoubleInitializationError()
    parent = scope
    actions.forEach { it.initialize(this) }
  }

  override fun clone(): ServerDrivenEvent {
    if (parent != null) throw CloneAfterInitializationError()
    val cloned = ServerDrivenEvent(name)
    cloned.actions = actions.map { it.clone() }
    return cloned
  }
}
