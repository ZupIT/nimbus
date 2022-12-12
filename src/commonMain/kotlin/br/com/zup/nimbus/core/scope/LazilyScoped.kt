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

/**
 * An entity that needs a scope, but the scope can't be known upon construction, i.e. it must be lazily initialized.
 * LazilyScoped entities must also be able to copy themselves as long as a scope hasn't been assigned yet.
 */
interface LazilyScoped<T> {
  /**
   * Initializes this entity with the given scope.
   *
   * @throws DoubleInitializationError if called more than once.
   */
  fun initialize(scope: Scope)
  /**
   * Deep copies this entity. This can only be called before initialize.
   *
   * @throws CloneAfterInitializationError if called after the initialization.
   */
  fun clone(): T
}

class DoubleInitializationError: IllegalStateException(
  "Can't initialize this LazilyScoped instance because it has already been initialized!"
)

class CloneAfterInitializationError: IllegalStateException(
  "Can't clone this LazilyScoped instance because it has already been initialized!!"
)
