/*
 * Copyright 2023 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
