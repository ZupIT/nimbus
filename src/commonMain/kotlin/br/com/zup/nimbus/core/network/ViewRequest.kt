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

data class ViewRequest(
  /**
   * The URL to send the request to. When it starts with "/", it's relative to the BaseUrl.
   */
  val url: String,
  /**
   * The request method. Default is "Get".
   */
  val method: ServerDrivenHttpMethod = ServerDrivenHttpMethod.Get,
  /**
   * The headers for the request.
   */
  val headers: Map<String, String>? = null,
  /**
   * The request body. Invalid for "Get" requests.
   */
  val body: String? = null,
  /**
   * UI tree to show if an error occurs and the view can't be fetched.
   */
  val fallback: Map<String, Any?>? = null,
  /**
   * The map of state ids and its values that will be used on the next page.
   */
  val params: Map<String, Any?>? = null,
)
