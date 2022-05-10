package com.zup.nimbus.core.tree

class ObservableState(id: String, value: Any?): ServerDrivenState(id, value, null) {
  private val listeners = ArrayList<() -> Unit>()

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
