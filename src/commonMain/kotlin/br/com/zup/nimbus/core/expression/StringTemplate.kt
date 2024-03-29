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

import br.com.zup.nimbus.core.dependency.CommonDependency
import br.com.zup.nimbus.core.dependency.Dependent
import br.com.zup.nimbus.core.scope.CloneAfterInitializationError
import br.com.zup.nimbus.core.scope.DoubleInitializationError
import br.com.zup.nimbus.core.scope.LazilyScoped
import br.com.zup.nimbus.core.scope.Scope

class StringTemplate(
  private val composition: List<Expression>,
): Expression, CommonDependency(), Dependent, LazilyScoped<StringTemplate> {
  private var value: String = ""
  private var hasInitialized = false

  override fun initialize(scope: Scope) {
    if (hasInitialized) throw DoubleInitializationError()
    composition.forEach {
      if (it is LazilyScoped<*>) it.initialize(scope)
      if (it is CommonDependency) it.addDependent(this)
    }
    hasInitialized = true
    update()
    hasChanged = false
  }

  override fun getValue(): String {
    return value
  }

  override fun update() {
    val newValue = composition.joinToString("") { "${it.getValue() ?: ""}" }
    if (value != newValue) {
      value = newValue
      hasChanged = true
    }
  }

  override fun clone(): StringTemplate {
    if (hasInitialized) throw CloneAfterInitializationError()
    val clonedComposition = composition.map { if (it is LazilyScoped<*>) it.clone() as Expression else it }
    return StringTemplate(clonedComposition)
  }
}
