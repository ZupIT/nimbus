package com.zup.nimbus.core.tree

interface ServerDrivenNode {
  val id: String
  val component: String
  val properties: Map<String, Any?>?
  val children: List<ServerDrivenNode>?
}
