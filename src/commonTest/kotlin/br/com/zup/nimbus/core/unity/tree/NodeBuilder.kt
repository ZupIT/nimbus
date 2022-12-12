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

package br.com.zup.nimbus.core.unity.tree

import br.com.zup.nimbus.core.EmptyHttpClient
import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.ServerDrivenConfig
import br.com.zup.nimbus.core.tree.dynamic.builder.MalformedJsonError
import kotlin.test.Test
import kotlin.test.assertTrue

class NodeBuilderTest {
  val nimbus = Nimbus(ServerDrivenConfig(baseUrl = "", platform = "test", httpClient = EmptyHttpClient))

  @Test
  fun `should throw when json is invalid`() {
    var error: Throwable? = null
    try {
      nimbus.nodeBuilder.buildFromJsonString("""{ "aa": 45, 85,""")
    } catch (e: Throwable) {
      error = e
    }
    assertTrue(error is MalformedJsonError)
  }
}
