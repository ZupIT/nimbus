package com.zup.nimbus.core.unit

import kotlin.reflect.KClass

class UnexpectedDataTypeError(val path: String, val expectedType: KClass<*>, val valueFound: Any?) : Error() {
  override val message: String

  init {
    val at = if (path.isEmpty()) "" else """ at "$path""""
    val expected = expectedType.simpleName
    val found = if (valueFound == null) "null" else valueFound::class.simpleName
    message = """Unexpected value type$at. Expected "$expected", found "$found"."""
  }
}

inline fun <reified T>valueOf(data: Any?, path: String = ""): T {
  var current: Any? = data
  val accessors = if (path.isEmpty()) emptyList<String>().iterator() else path.split(".").iterator()
  while (current != null && accessors.hasNext()) {
    current = if (current !is Map<*, *>) null else current[accessors.next()]
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
