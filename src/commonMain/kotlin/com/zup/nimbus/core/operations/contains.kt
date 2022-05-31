package com.zup.nimbus.core.operations

fun contains(list: List<Any>, item: Any): Boolean {
  return list.contains(item)
}

fun contains(text: String, term: String): Boolean {
  return text.contains(term)
}
