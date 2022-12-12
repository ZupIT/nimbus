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

package br.com.zup.nimbus.core.network

import br.com.zup.nimbus.core.tree.dynamic.builder.MalformedComponentError
import br.com.zup.nimbus.core.tree.dynamic.builder.MalformedJsonError
import br.com.zup.nimbus.core.tree.dynamic.node.RootNode
import kotlin.coroutines.cancellation.CancellationException

interface ViewClient {
  /**
   * Fetches a view (UI tree) from the server.
   *
   * @param request the data for the request to make.
   * @return the UI tree fetched.
   * @throws RequestError if an error happens while creating the request object.
   * @throws ResponseError if an error happens while obtaining the response from the server.
   * @throws MalformedJsonError if the string is not a valid json.
   * @throws MalformedComponentError if an error happens while deserializing the response into a RenderNode.
   */
  @Throws(RequestError::class, ResponseError::class, MalformedJsonError::class, MalformedComponentError::class,
    CancellationException::class)
  suspend fun fetch(request: ViewRequest): RootNode

  /**
   * Pre-fetches a view (UI tree) from the server and stores it. The next fetch will get the stored value instead of
   * performing a network request.
   *
   * Each implementation of the ViewClient decides how its pre-fetch logic works. See `DefaultViewClient` for the
   * default logic.
   *
   * @param request the data for the request to make.
   */
  fun preFetch(request: ViewRequest)
}
