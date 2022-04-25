package com.zup.nimbus.core.utils

fun <K, V, T>mapValuesToMutableMap(map: Map<K, V>, iteratee: (entry: Map.Entry<K, V>) -> T): MutableMap<K, T> {
  val result = HashMap<K, T>()
  map.forEach { result[it.key] = iteratee(it) }
  return result
}

fun setMapValue(map: MutableMap<*, *>, path: String, newValue: Any) {
  // todo
}
