package br.com.zup.nimbus.core.tree

class DefaultIdManager : IdManager {
  private val prefix = "nimbus"
  private var current = 1

  override fun next(): String {
    return "$prefix:${current++}"
  }
}
