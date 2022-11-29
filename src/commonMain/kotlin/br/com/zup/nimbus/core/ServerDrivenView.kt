package br.com.zup.nimbus.core

import br.com.zup.nimbus.core.scope.CommonScope

/**
 * A scope for the current view in a navigator.
 */
class ServerDrivenView(
  /**
   * The parent nimbus scope.
   */
  val nimbus: Nimbus,
  /**
   * The states in this scope. Useful for creating view parameters in the navigation.
   */
  states: List<ServerDrivenState>? = null,
  /**
   * A description for this view. Suggestion: the URL used to load the content of this view or "json", if a local json
   * string was used to load it.
   */
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
