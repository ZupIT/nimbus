package com.zup.nimbus.core.utils

import kotlin.reflect.KClass

val dataPathRegex = """(?:^\w+)|(?:\.\w+)|\[\d+\]""".toRegex()

class InvalidDataPathError(path: String, cause: String): Error() {
  override val message = "Error while obtaining data from a path. The following path is invalid: $path.\nCause: $cause}"
}

class UnexpectedDataTypeError(val path: String, val expectedType: KClass<*>, val valueFound: Any?) : Error() {
  override val message: String

  init {
    val at = if (path.isEmpty()) "" else """ at "$path""""
    val expected = expectedType.simpleName
    val found = if (valueFound == null) "null" else valueFound::class.simpleName
    message = """Unexpected value type$at. Expected "$expected", found "$found"."""
  }
}

fun extractValueOfArray(data: Any, accessor: String, path: String): Any? {
  try {
    val index = ("""\d+""".toRegex().find(accessor)!!.value).toInt()
    return if (data is List<*> && index < data.size) data[index] else null
  } catch (e: Throwable) {
    throw if (e is NumberFormatException || e is NullPointerException) {
      InvalidDataPathError(path, "Expected a number as an array index: $accessor")
    } else e
  }
}

fun extractValueOfMap(data: Any, accessor: String): Any? {
  val key = accessor.replace(".", "")
  return if (data is Map<*, *>) data[key] else null
}

/**
 * Returns `data` at `path` casted to the expected type (T).
 *
 * If `path` is empty and `data` can be casted to T, `data` is returned. If `data` can't be casted to T,
 * UnexpectedDataTypeError is thrown.
 *
 * `path` specifies which property of the data structure we need. If `data` is a primitive type and `path` is not empty,
 * null is returned, because we can't get a path from a primitive type. If `data` is a map and `path` is "foo", the
 * value of the key "foo" is returned. If `data` is a list and `path` is `"[0]"`, the first position of the array is
 * returned.
 *
 * The path can be composed of keys (.key) and indexes (`[index]`). Example: `"foo.bar[0][1].text"`. In this example,
 * `data` must be a map with the key "foo", which must be a map with the key "bar", which must be a list where the
 * first position is another list where the second position is a map. The property "text" of this last map is casted to
 * T and returned.
 *
 * If `path` doesn't exist in `data` and T is nullable, null is returned. If T is not nullable, UnexpectedDataTypeError
 * is thrown.
 *
 * This function is not responsible for validating the path, but if it does encounter a problem, InvalidDataPathError
 * is thrown.
 *
 * @param data the data source to get the return value from.
 * @param path the path to the item we want in `data`.
 * @throws InvalidDataPathError if an error is encountered while evaluating the path string.
 * @throws UnexpectedDataTypeError if the type of the encountered result (or null if no result is encountered) is not
 * the same as the expected type (T).
 */
@Throws(InvalidDataPathError::class, UnexpectedDataTypeError::class)
inline fun <reified T>valueOf(data: Any?, path: String = ""): T {
  var current: Any? = data
  val accessors = dataPathRegex.findAll(path).iterator()
  while (current != null && accessors.hasNext()) {
    val accessor = accessors.next().value
    val isArray = accessor.startsWith("[")
    current = if (isArray) extractValueOfArray(current, accessor, path) else extractValueOfMap(current, accessor)
  }
  if (accessors.hasNext()) current = null
  try {
    @Suppress("UNCHECKED_CAST")
    return current as T
  } catch (e: Throwable) {
    if (e is ClassCastException || e is NullPointerException) {
      throw UnexpectedDataTypeError(path, T::class, current)
    }
    throw e
  }
}

/**
 * Recursively copies a value if it's a list or a map. Otherwise, it returns the received value.
 *
 * Attention: the copied lists and maps are immutable.
 *
 * @param value the value to copy.
 * @return the copied value.
 */
fun deepCopy(value: Any?): Any? {
  if (value is Map<*, *>) return value.mapValues { deepCopy(value) }
  if (value is List<*>) return value.map { deepCopy(it) }
  return value
}
