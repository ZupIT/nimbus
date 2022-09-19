package com.zup.nimbus.core.tree

import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.ServerDrivenView
import com.zup.nimbus.core.scope.Scope
import com.zup.nimbus.core.tree.node.ServerDrivenNode

interface ServerDrivenEvent: Scope {
  val name: String
  val node: ServerDrivenNode
  val view: ServerDrivenView
  val nimbus: Nimbus
  fun run()
  fun run(implicitStateValue: Any?)
}
