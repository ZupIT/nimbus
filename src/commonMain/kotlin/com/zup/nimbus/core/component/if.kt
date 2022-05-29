package com.zup.nimbus.core.component

import com.zup.nimbus.core.tree.RenderNode

/*
interface If {
  condition: boolean,
  children: Then | [Then, Else],
}

interface Then {
  children: Component[],
}

interface Else {
  children: Component[],
}
*/

fun ifComponent(node: RenderNode): List<RenderNode> {
  // todo
  print(node)
  return emptyList()
}
