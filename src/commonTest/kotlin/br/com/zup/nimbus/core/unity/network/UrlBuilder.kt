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

package br.com.zup.nimbus.core.unity.network

import br.com.zup.nimbus.core.EmptyHttpClient
import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.ServerDrivenConfig
import br.com.zup.nimbus.core.network.DefaultUrlBuilder
import br.com.zup.nimbus.core.unity.network.mocks.CustomUrlBuilderTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class UrlBuilderTest {
  private val builder = DefaultUrlBuilder("/base-url/")

  @Test
  fun `should remove the last slash from the base url when it ends with one`() {
    val result = builder.build("/new-path")
    assertNotEquals("/base-url//new-path", result)
    assertEquals("/base-url/new-path", result)
  }

  @Test
  fun `should return only the path when the path to build doesn't start with a slash`() {
    val result = builder.build("new-path")
    assertNotEquals("/base-url/new-path", result)
    assertEquals("new-path", result)
  }

  @Test
  fun `should use the custom url builder when nimbus was instantiated with one`() {
    val nimbus = Nimbus(
      ServerDrivenConfig(
        baseUrl = "/",
        platform = "test",
        urlBuilder = { CustomUrlBuilderTest() },
        httpClient = EmptyHttpClient,
      )
    )
    val result = nimbus.urlBuilder.build("new-path")
    assertEquals("/custom-builder/new-path", result)
  }
}
