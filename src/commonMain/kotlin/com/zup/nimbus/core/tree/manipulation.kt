package com.zup.nimbus.core.tree

import com.zup.nimbus.core.render.TreeUpdateMode

fun <T: ServerDrivenNode<T>>replaceInTree(target: T, source: T, anchor: String) {
  throw Error("Not Implemented yet!")
}

// returns the node that will receive the new branch (source). This is the node "anchor" refers to
fun <T: ServerDrivenNode<T>>insertIntoTree(target: T, source: T, anchor: String, mode: TreeUpdateMode): T {
  throw Error("Not Implemented yet!")
}
