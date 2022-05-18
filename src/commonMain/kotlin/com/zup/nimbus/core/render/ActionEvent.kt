package com.zup.nimbus.core.render

import com.zup.nimbus.core.tree.RenderNode
import com.zup.nimbus.core.tree.ServerDrivenAction

data class ActionEvent(
  /**
   * The action of this event. Use this object to find the name of the action, its properties and metadata.
   */
  val action: ServerDrivenAction,
  /**
   * The name of the event that triggers the action, i.e. the key of the node property that declared it. Example, a
   * component "Button" triggers the action found in the property "onPress" when it's pressed. "onPress" is the
   * "name" of this event.
   */
  val name: String,
  /**
   * The node (component) containing the action.
   */
  val node: RenderNode,
  /**
   * The view containing the node that declared the action.
   */
  val view: ServerDrivenView,
)
