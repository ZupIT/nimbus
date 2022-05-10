package com.zup.nimbus.core.tree

import com.zup.nimbus.core.utils.deepCopy
import com.zup.nimbus.core.utils.setMapValue

open class ServerDrivenState(
  /**
   * The id of the state.
   */
  val id: String,
  /**
   * The value of the state.
   *
   * Note: do not set this directly. Use the method "set" instead.
   */
  internal var value: Any?,
  /**
   * The node that declared this state. This must be null if the state has no parent.
   */
  val parent: RenderNode?,
) {
  /**
   * Gets the current value. This is a copy because this object can't be mutated by third-parties outside the set
   * function.
   *
   * @return a copy of the current value of this state.
   */
  fun getValueCopy(): Any? {
   return deepCopy(value)
  }

  /**
   * Changes the value of this state at the provided path.
   *
   * @param newValue the new value of "state.value.$path". Must be encodable, i.e. null, string, number, boolean,
   * Map<string, encodable> or List<encodable>.
   * @param path the path within the state to modify. Example: "" to alter the entire state. "foo.bar" to alter the
   * property "bar" of "foo" in the map "state.value". If "path" is not empty and "state.value" is not a mutable map, it
   * is converted to one. The path must only contain letters, numbers and underscores separated by dots.
   */
  open fun set(newValue: Any?, path: String) {
    if (path.isEmpty()) {
      value = newValue
    } else {
      if (value !is MutableMap<*, *>) value = HashMap<String, Any>()
      setMapValue(value as MutableMap<*, *>, path, newValue)
    }
  }
}
