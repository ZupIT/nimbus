package com.zup.nimbus.core.tree

data class ServerDrivenState(
  /**
   * The id of the state.
   */
  val id: String,
  /**
   * The value of the state.
   */
  var value: Any?,
  /**
   * The node that declared this state.
   */
  val parent: RenderNode,
)
