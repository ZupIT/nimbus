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

package br.com.zup.nimbus.core.scope

import br.com.zup.nimbus.core.ServerDrivenState

/**
 * A simple scope to group up server driven nodes with common states without having to create a new node. Used mostly by
 * the ForEach component when making the states "item" and "index" available to its children.
 */
class StateOnlyScope(override var parent: Scope?, override val states: List<ServerDrivenState>?): Scope {
  override fun get(key: String) = parent?.get(key)

  override fun set(key: String, value: Any) {
    parent?.set(key, value)
  }

  override fun unset(key: String) {
    parent?.unset(key)
  }
}
