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

import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.OperationHandler
import br.com.zup.nimbus.core.dependency.CommonDependency
import br.com.zup.nimbus.core.dependency.Dependent
import br.com.zup.nimbus.core.log.Logger
import br.com.zup.nimbus.core.scope.CloneAfterInitializationError
import br.com.zup.nimbus.core.scope.DoubleInitializationError
import br.com.zup.nimbus.core.scope.LazilyScoped
import br.com.zup.nimbus.core.scope.Scope
import br.com.zup.nimbus.core.scope.closestScopeWithType

class Operation(
  private val handler: OperationHandler,
  private val arguments: List<Expression>,
  private val detached: Boolean = false,
): Expression, CommonDependency(), Dependent, LazilyScoped<Operation> {
  private var value: Any? = null
  private var hasInitialized = false
  private var getLogger: (() -> Logger?)? = null

  override fun initialize(scope: Scope) {
    if (hasInitialized) throw DoubleInitializationError()
    getLogger = { scope.closestScopeWithType<Nimbus>()?.logger }
    arguments.forEach {
      if (it is LazilyScoped<*>) it.initialize(scope)
      if (it is CommonDependency && !detached) it.addDependent(this)
    }
    hasInitialized = true
    if (!detached) {
      update()
      hasChanged = false
    }
  }

  override fun update() {
    val argValues = arguments.map {
      if (detached && it is Dependent) it.update()
      it.getValue()
    }
    val newValue = try {
      handler(argValues)
    } catch (@Suppress("TooGenericExceptionCaught") t: Throwable) {
      getLogger?.let { it()?.error(t.message ?: t.stackTraceToString()) }
      null
    }
    if (value != newValue) {
      value = newValue
      hasChanged = true
    }
  }

  override fun getValue(): Any? {
    return value
  }

  override fun clone(): Operation {
    if (hasInitialized) throw CloneAfterInitializationError()
    val clonedArguments = arguments.map { if (it is LazilyScoped<*>) it.clone() as Expression else it }
    return Operation(handler, clonedArguments)
  }
}
