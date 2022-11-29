package br.com.zup.nimbus.core.unity

import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.ServerDrivenState
import br.com.zup.nimbus.core.ServerDrivenView
import br.com.zup.nimbus.core.dependency.CommonDependency
import br.com.zup.nimbus.core.scope.CommonScope
import br.com.zup.nimbus.core.scope.Scope
import br.com.zup.nimbus.core.scope.closestScopeWithType
import br.com.zup.nimbus.core.tree.ServerDrivenAction
import br.com.zup.nimbus.core.tree.ServerDrivenEvent
import br.com.zup.nimbus.core.tree.ServerDrivenNode

class SimpleEvent(
  override val name: String = "mock",
  parent: Scope? = null,
  states: List<ServerDrivenState>? = null,
): ServerDrivenEvent, CommonDependency(), Scope by CommonScope(states, parent) {
  var calls: MutableList<Any?> = mutableListOf()
  override val actions: List<ServerDrivenAction> = emptyList()
  override val node: ServerDrivenNode
    get() = throw NotImplementedError()
  override val view: ServerDrivenView
    get() = throw NotImplementedError()
  override val nimbus: Nimbus
    get() = closestScopeWithType() ?: throw IllegalStateException("Nimbus is not available")

  override fun run() {
    calls.add(null)
  }

  override fun run(implicitStateValue: Any?) {
    calls.add(implicitStateValue)
  }

  fun clear() {
    calls = mutableListOf()
  }
}
