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

package br.com.zup.nimbus.core.ui.operations

import br.com.zup.nimbus.core.ui.UILibrary

internal fun registerObjectOperations(library: UILibrary) {
  library
    .addOperation("object") {
      val objectMap = mutableMapOf<String, Any?>()
      for (i in it.indices step 2) {
        objectMap[it.getOrNull(i).toString()] = it.getOrNull(i + 1)
      }
      objectMap
    }
    .addOperation("entries") {
      val result = it.firstOrNull()?.let { map ->
        if (map is Map<*, *>) map.entries.map { entry -> mapOf("key" to entry.key, "value" to entry.value) }
        else null
      }
      result ?: emptyList<Any>()
    }
}
