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
