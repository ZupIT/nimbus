package com.zup.nimbus.core.tree.stateful

import com.zup.nimbus.core.ServerDrivenState

interface Stateful {
  val states: List<ServerDrivenState>?
  val parent: Stateful?
}

fun Stateful.find(id: String): ServerDrivenState? {
  val found = this.states?.find { it.id == id }
  if (found != null) return found
  this.parent?.let { return it.find(id) }
  return null
}

