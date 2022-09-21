package com.zup.nimbus.core.unity.action

import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.ServerDrivenState
import com.zup.nimbus.core.ServerDrivenView
import com.zup.nimbus.core.dependency.CommonDependency
import com.zup.nimbus.core.scope.CommonScope
import com.zup.nimbus.core.scope.Scope
import com.zup.nimbus.core.scope.closestScopeWithType
import com.zup.nimbus.core.tree.ServerDrivenAction
import com.zup.nimbus.core.tree.ServerDrivenEvent
import com.zup.nimbus.core.tree.ServerDrivenNode

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
