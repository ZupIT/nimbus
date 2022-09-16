package com.zup.nimbus.core

import com.zup.nimbus.core.scope.CommonScope

class ServerDrivenView(
  val nimbus: Nimbus,
  /**
   * A description for this view. Suggestion: the URL used to load the content of this view or "json", if a local json
   * string was used to load it.
   */
  states: List<ServerDrivenState>? = null,
  val description: String? = null,
  /**
   * A function to get the navigator that spawned this view.
   *
   * Attention: this is a function so we can prevent a cyclical reference between Kotlin Native and Swift. Replacing
   * this with a direct reference will cause memory leaks.
   */
  getNavigator: () -> ServerDrivenNavigator,
): CommonScope(parent = nimbus, states = states) {
  constructor(nimbus: Nimbus, getNavigator: () -> ServerDrivenNavigator):
    this(nimbus, null, null, getNavigator)

  val navigator = getNavigator()
}
