package com.zup.nimbus.core.render

import com.zup.nimbus.core.tree.RawNode
import com.zup.nimbus.core.tree.RenderedNode

typealias LifecycleHandler<T> = (tree: T) -> Unit

data class LifecycleHook(
  val beforeViewSnapshot: LifecycleHandler<RawNode>? = null,
  val afterViewSnapshot: LifecycleHandler<RawNode>? = null,
  val beforeRender: LifecycleHandler<RenderedNode>? = null,
)
