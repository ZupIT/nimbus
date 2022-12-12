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

open class NetworkError(override val message: String): Error(message)

class RequestError(
  message: String?,
): NetworkError("Error while sending the request.${if (message == null) "" else " See the details below:\n$message"}")

class ResponseError(
  val status: Int,
  val body: String?,
): NetworkError("Request failed with status $status.${if (body == null) "" else "\n$body"}")
