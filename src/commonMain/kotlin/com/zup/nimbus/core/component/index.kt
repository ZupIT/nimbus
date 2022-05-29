package com.zup.nimbus.core.component

import com.zup.nimbus.core.tree.RenderNode

val coreComponents: Map<String, (node: RenderNode) -> List<RenderNode>> = mapOf(
  "forEach" to { forEachComponent(it) },
  "if" to { ifComponent(it) },
  "switch" to { switchComponent(it) }
)
