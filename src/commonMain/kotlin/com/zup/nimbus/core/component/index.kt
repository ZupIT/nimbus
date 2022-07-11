package com.zup.nimbus.core.component

import com.zup.nimbus.core.tree.RenderNode

fun getCoreComponents(): Map<String, (node: RenderNode) -> List<RenderNode>> {
   return mapOf(
    "forEach" to { forEachComponent(it) },
    "if" to { ifComponent(it) },
    "switch" to { switchComponent(it) },
  )
}
