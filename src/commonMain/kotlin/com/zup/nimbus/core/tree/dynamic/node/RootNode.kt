package com.zup.nimbus.core.tree.dynamic.node

import com.zup.nimbus.core.ServerDrivenView

/**
 * A RootNode for a UI Tree. Used to wrap every tree yielded by a NodeBuilder.
 *
 * A RootNode is important to:
 * 1. allow the use of polymorphic nodes in the root of the original json;
 * 2. have an immutable reference to the current UI, even if its content is refreshed.
 *
 * A RootNode is rendered as a fragment. Fragment is the only UI component required by Nimbus Core to be implemented
 * by the UI Layer. It must be a simple column aligned in the top left corner without any kind of styling.
 *
 * Root node always have the same id: "nimbus:root".
 */
class RootNode : DynamicNode("nimbus:root", "fragment", null, false) {
  /**
   * Replaces the current content of this root node and initializes it with the view passed as parameter.
   * This is useful for performing a refresh operation on a Server Driven Screen.
   */
  fun replaceContent(newContent: RootNode, view: ServerDrivenView) {
    childrenContainer = newContent.childrenContainer
    childrenContainer?.initialize(view)
  }
}
