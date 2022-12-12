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

package br.com.zup.nimbus.core.expression

import br.com.zup.nimbus.core.ServerDrivenState
import br.com.zup.nimbus.core.dependency.CommonDependency
import br.com.zup.nimbus.core.dependency.Dependent
import br.com.zup.nimbus.core.scope.CloneAfterInitializationError
import br.com.zup.nimbus.core.scope.DoubleInitializationError
import br.com.zup.nimbus.core.scope.LazilyScoped
import br.com.zup.nimbus.core.scope.Scope
import br.com.zup.nimbus.core.scope.closestState
import br.com.zup.nimbus.core.utils.valueOfPath

class StateReference(
  private var id: String,
  private val path: String,
  private var onNotFound: ((String, Scope) -> Unit)? = null,
): Expression, CommonDependency(), Dependent, LazilyScoped<StateReference> {
  private var state: ServerDrivenState? = null
  private var value: Any? = null

  override fun initialize(scope: Scope) {
    if (state != null) throw DoubleInitializationError()
    state = scope.closestState(id)
    if (state == null) onNotFound?.let { it(id, scope) }
    update()
    hasChanged = false
    state?.addDependent(this)
    onNotFound = null
  }

  override fun getValue(): Any? {
    return value
  }

  override fun update() {
    val newValue: Any? = valueOfPath(state?.value, path)
    if (value != newValue) {
      value = newValue
      hasChanged = true
    }
  }

  override fun clone(): StateReference {
    if (state != null) throw CloneAfterInitializationError()
    return StateReference(id, path, onNotFound)
  }
}
