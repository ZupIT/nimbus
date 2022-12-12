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

open class CommonScope(
  override val states: List<ServerDrivenState>?,
  override var parent: Scope? = null,
): Scope {
  private val storage = mutableMapOf<String, Any>()

  override fun get(key: String): Any? {
    return storage[key] ?: parent?.get(key)
  }

  override fun set(key: String, value: Any) {
    storage[key] = value
  }

  override fun unset(key: String) {
    storage.remove(key)
  }
}
