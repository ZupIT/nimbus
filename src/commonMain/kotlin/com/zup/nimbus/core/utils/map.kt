package com.zup.nimbus.core.utils

fun <K, V, T>mapValuesToMutableMap(map: Map<K, V>, iteratee: (entry: Map.Entry<K, V>) -> T): MutableMap<K, T> {
  val result = HashMap<K, T>()
  map.forEach { result[it.key] = iteratee(it) }
  return result
}

fun <K, V>deepCopyToMutableMap(map: Map<K, V>): MutableMap<K, V> {
  val result = HashMap<K, V>()
  throw Error("Not implemented")
}
