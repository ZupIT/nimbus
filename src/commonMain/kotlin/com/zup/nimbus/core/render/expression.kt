package com.zup.nimbus.core.render

import com.zup.nimbus.core.tree.ServerDrivenState

fun containsExpression(value: String): Boolean {
  return false // todo: matches the regex
}

fun resolveExpressions(value: String, stateHierarchy: List<ServerDrivenState>?) {
  throw Error("Not implemented yet!")
}
