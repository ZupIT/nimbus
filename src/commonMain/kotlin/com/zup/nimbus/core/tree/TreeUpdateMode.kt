package com.zup.nimbus.core.tree

enum class TreeUpdateMode {
  /**
   * Adds the new node before the current set of children, i.e. at the first position of the array.
   */
  Prepend,
  /**
   * Adds the new node after the current set of children, i.e. at the last position of the array.
   */
  Append,
  /**
   * Replaces the current set of children by the new node.
   */
  Replace,
  /**
   * Replaces the node itself, not its children, by the new node.
   */
  ReplaceItself,
}
