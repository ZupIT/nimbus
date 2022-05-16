package com.zup.nimbus.core.tree

class ObservableState(id: String, value: Any?): ServerDrivenState(id, value, null) {
  private val listeners = ArrayList<() -> Unit>()

  /**
   * Listens to changes to this state's value.
   *
   * @param listener the function to run when the value of this state changes.
   * @return a function to remove this listener.
   */
  fun onChange(listener: () -> Unit): (() -> Unit) {
    listeners.add(listener)
    return { listeners.remove(listener) }
  }

  override fun set(newValue: Any?, path: String) {
    // if the new value is a map or list, it must be mutable.
    var mutableValue = newValue
    if (newValue is Map<*, *> && newValue !is MutableMap<*, *>) mutableValue = newValue.toMutableMap()
    else if (newValue is List<*> && newValue !is MutableList<*>) mutableValue = newValue.toMutableList()
    // super
    super.set(mutableValue, path)
    // notify
    listeners.forEach { it() }
  }

  fun set(newValue: Any?) {
    set(newValue, "")
  }
}
