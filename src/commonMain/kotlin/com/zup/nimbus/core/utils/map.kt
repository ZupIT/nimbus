package com.zup.nimbus.core.utils

/**
 * Equivalent to the mapValues operation of a Map, but instead of creating a Map as a result, it creates a MutableMap.
 *
 * Note: this method is important for performance reasons, please don't replace it for less code, but higher cost.
 *
 * @param map the map to have its values mapped.
 * @param iteratee the function to map each value of "map". It receives the entry and must return the new value.
 * @return a mutable map with every value of "map" mapped to the result of "iteratee(entry)".
 */
fun <K, V, T>mapValuesToMutableMap(map: Map<K, V>, iteratee: (entry: Map.Entry<K, V>) -> T): MutableMap<K, T> {
  val result = HashMap<K, T>()
  map.forEach { result[it.key] = iteratee(it) }
  return result
}

/**
 * Sets "newValue" in "map" according to "path". The "path" is a string telling which key of the map should be set, but
 * instead of specifying a single key, it specifies a set of keys that goes deep into the map. For instance, if
 * "map["foo"]" is a map and we want set the key "bar" of the map "foo", the path will be "foo.bar".
 *
 * @param map the map to set.
 * @param path the path to set within the map. This must contain only letters, numbers and underscore separated by dots.
 * This must also not be empty. If the path is invalid InvalidDataPathError is thrown.
 * @param newValue the new value to set for "map.$path".
 * @throws InvalidDataPathError if the path is invalid
 */
fun setMapValue(map: MutableMap<String, Any?>, path: String, newValue: Any?) {
  val regex = """(\w+)(?:\.(.+))?""".toRegex()
  val matchResult =
    regex.find(path) ?: throw InvalidDataPathError(path, "Doesn't comply with the regex ${regex.pattern}")
  val (current, next) = matchResult.destructured
  if (next.isEmpty()) {
    map[current] = newValue
  } else {
    if (map[current] !is MutableMap<*, *>) map[current] = HashMap<String, Any>()
    setMapValue(map[current] as MutableMap<String, Any?>, next, newValue)
  }
}
