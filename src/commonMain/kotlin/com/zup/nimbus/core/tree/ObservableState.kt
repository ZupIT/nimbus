package com.zup.nimbus.core.tree

typealias ObservableStateListener = (value: Any?) -> Unit
typealias RemovableObservableStateListener = () -> Unit

class ObservableState(id: String, value: Any?): ServerDrivenState(id, value, null) {
  private val listeners = ArrayList<ObservableStateListener>()

  /**
   * Listens to changes to this state's value.
   *
   * @param listener the function to run when the value of this state changes.
   * @return a function to remove this listener.
   */
  fun onChange(listener: ObservableStateListener): RemovableObservableStateListener {
    listeners.add(listener)
    return { listeners.remove(listener) }
  }

  override fun set(newValue: Any?, path: String) {
    super.set(newValue, path)
    listeners.forEach { it(super.value) }
  }

  fun set(newValue: Any?) {
    set(newValue, "")
  }
}
