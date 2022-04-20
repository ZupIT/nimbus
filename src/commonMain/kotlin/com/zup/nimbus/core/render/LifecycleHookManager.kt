package com.zup.nimbus.core.render

import com.zup.nimbus.core.tree.RawNode
import com.zup.nimbus.core.tree.RenderedNode

class LifecycleHookManager(hooks: List<LifecycleHook>? = null) {
  private val beforeViewSnapshotHandlers = ArrayList<LifecycleHandler<RawNode>>()
  private val afterViewSnapshotHandlers = ArrayList<LifecycleHandler<RawNode>>()
  private val beforeRenderHandlers = ArrayList<LifecycleHandler<RenderedNode>>()

  init {
    hooks?.forEach { add(it) }
  }

  fun add(hook: LifecycleHook) {
    if (hook.beforeViewSnapshot != null) beforeViewSnapshotHandlers.add(hook.beforeViewSnapshot)
    if (hook.afterViewSnapshot != null) afterViewSnapshotHandlers.add(hook.afterViewSnapshot)
    if (hook.beforeRender != null) beforeRenderHandlers.add(hook.beforeRender)
  }

  internal fun runBeforeViewSnapshot(tree: RawNode) {
    beforeViewSnapshotHandlers.forEach { it(tree) }
  }

  internal fun runAfterViewSnapshot(tree: RawNode) {
    afterViewSnapshotHandlers.forEach { it(tree) }
  }

  internal fun runBeforeRender(tree: RenderedNode) {
    beforeRenderHandlers.forEach { it(tree) }
  }
}
