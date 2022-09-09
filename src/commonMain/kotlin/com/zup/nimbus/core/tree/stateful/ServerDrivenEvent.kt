package com.zup.nimbus.core.tree.stateful

import com.zup.nimbus.core.dependencyGraph.Dependency
import com.zup.nimbus.core.dependencyGraph.updateDependentsOf
import com.zup.nimbus.core.ActionTriggeredEvent
import com.zup.nimbus.core.ServerDrivenView
import com.zup.nimbus.core.tree.ServerDrivenAction
import com.zup.nimbus.core.ServerDrivenState
import com.zup.nimbus.core.scope.EventScopeImpl
import com.zup.nimbus.core.scope.ViewScope

class ServerDrivenEvent(
  val name: String,
  override val parent: Stateful,
  viewScope: ViewScope,
): Stateful {
  var actions: List<ServerDrivenAction> = emptyList()
  override val states = listOf(ServerDrivenState(name, null))
  internal val scope = EventScopeImpl(viewScope, this)

  fun run() {
    val dependencies = mutableSetOf<Dependency>()
    actions.forEach { it.handler(ActionTriggeredEvent(action = it, dependencies = dependencies, scope = scope)) }
    updateDependentsOf(dependencies)
  }

  fun run(implicitStateValue: Any?) {
    states.first().set(implicitStateValue)
    updateDependentsOf(states.toSet())
    run()
  }
}
