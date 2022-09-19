package com.zup.nimbus.core

import com.zup.nimbus.core.dependency.CommonDependency
import com.zup.nimbus.core.dependency.updateDependentsOf
import com.zup.nimbus.core.utils.deepCopyMutable
import com.zup.nimbus.core.utils.setMapValue
import com.zup.nimbus.core.utils.valueOfPath

class ServerDrivenState(
  /**
   * The id of the state.
   */
  val id: String,
  /**
   * The value of the state. Do not use this value as settable.
   * @see set, to set the new value of this state use the `set` function.
  */
  internal var value: Any?,
): CommonDependency() {
  /**
   * Gets the current value of this state. Do not use this value as settable.
   *
   * @see set, to set the new value of this state use the `set` function.
   * @return the current value of this state.
   */
  fun get(): Any? {
   return value
  }

  /**
   * If the value is a list or map, but is not mutable, returns a mutable version of it.
   * Otherwise, returns the input.
   */
  private fun getMutable(maybeImmutable: Any?): Any? {
    if (maybeImmutable is Map<*, *> && maybeImmutable !is MutableMap<*, *>) return maybeImmutable.toMutableMap()
    if (maybeImmutable is List<*> && maybeImmutable !is MutableList<*>) return maybeImmutable.toMutableList()
    return maybeImmutable
  }

  private fun setValueAtPath(newValue: Any?, path: String) {
    if (path.isEmpty()) {
      value = newValue
    } else {
      if (value !is MutableMap<*, *>) value = HashMap<String, Any>()
      @Suppress("UNCHECKED_CAST")
      setMapValue(value as MutableMap<String, Any?>, path, newValue)
    }
  }

  /**
   * Changes the value of this state at the provided path.
   *
   * @param newValue the new value of "state.value.$path". Must be encodable, i.e. null, string, number, boolean,
   * Map<string, encodable> or List<encodable>.
   * @param path the path within the state to modify. Example: "" to alter the entire state. "foo.bar" to alter the
   * property "bar" of "foo" in the map "state.value". If "path" is not empty and "state.value" is not a mutable map, it
   * is converted to one. The path must only contain letters, numbers and underscores separated by dots.
   * @param shouldUpdateDependents whether or not to propagate this change to everything that depends on the value of
   * this state. This is useful for making multiple changes to states and still have a single UI update. In this case,
   * you would pass false to every `set`, but the last one. By default, it will update its dependents.
   */
  fun set(newValue: Any?, path: String, shouldUpdateDependents: Boolean = true) {
    val currentValue: Any? = valueOfPath(value, path)
    if (currentValue != newValue) {
      val mutableValue = getMutable(newValue)
      setValueAtPath(mutableValue, path)
      hasChanged = true
      if (shouldUpdateDependents) updateDependentsOf(this)
    }
  }

  /**
   * Shortcut to `set(newValue, "")`, i.e. replaces the entire value of the state with `newValue`.
   */
  fun set(newValue: Any?) {
    set(newValue, "")
  }

  fun clone(): ServerDrivenState {
    return ServerDrivenState(id, deepCopyMutable(value))
  }
}
