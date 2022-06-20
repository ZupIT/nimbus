package com.zup.nimbus.core.component

import com.zup.nimbus.core.tree.RenderNode
import com.zup.nimbus.core.utils.valueOf

fun ifComponent(node: RenderNode): List<RenderNode> {
  val condition: Boolean = valueOf(node.properties, "condition")
  var thenNode: RenderNode? = null
  var elseNode: RenderNode? = null
  node.rawChildren?.forEach {
    when (it.component) {
      "then" -> thenNode = it
      "else" -> elseNode = it
      else -> throw UnexpectedComponentError("Component \"if\" should only have components \"then\" and \"else\" as " +
        "children. Found: \"${it.component}\".")
    }
  }
  if (thenNode == null) throw MissingComponentError("Component \"if\" must have a component \"then\" as child.")
  return (if (condition) thenNode?.rawChildren else elseNode?.rawChildren) ?: emptyList()
}
