package com.zup.nimbus.core.tree.stateful

import com.zup.nimbus.core.dependencyGraph.Dependency
import com.zup.nimbus.core.dependencyGraph.updateDependentsOf
import com.zup.nimbus.core.ActionTriggeredEvent
import com.zup.nimbus.core.ServerDrivenView
import com.zup.nimbus.core.tree.ServerDrivenAction
import com.zup.nimbus.core.ServerDrivenState

class ServerDrivenEvent(
  val name: String,
  override val parent: Stateful,
  val view: ServerDrivenView,
): Stateful {
  var actions: List<ServerDrivenAction> = emptyList()
  override val states = listOf(ServerDrivenState(name, null))

  fun run() {
    val dependencies = mutableSetOf<Dependency>()
    actions.forEach { it.handler(ActionTriggeredEvent(action = it, origin = this, dependencies = dependencies)) }
    updateDependentsOf(dependencies)
  }

  fun run(implicitStateValue: Any?) {
    states.first().set(implicitStateValue)
    updateDependentsOf(states.toSet())
    run()
  }
}
