package com.zup.nimbus.core.render

import com.zup.nimbus.core.tree.ServerDrivenNode

typealias LifecycleHook = (tree: ServerDrivenNode) -> ServerDrivenNode?

data class LifecycleHookManager(
  val beforeViewSnapshot: LifecycleHook,
  val afterViewSnapshot: LifecycleHook,
  val beforeRender: LifecycleHook,
)
