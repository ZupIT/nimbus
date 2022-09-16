package com.zup.nimbus.core.tree.node

import com.zup.nimbus.core.ServerDrivenView

class RootNode : DynamicNode("##ROOT##", "fragment", null, false) {
  /**
   * Replaces the current content of this root node and initializes it with the view passed as parameter.
   * This is useful for performing a refresh operation on a Server Driven Screen.
   */
  fun replaceContent(newContent: RootNode, view: ServerDrivenView) {
    childrenContainer = newContent.childrenContainer
    childrenContainer?.initialize(view)
  }
}
