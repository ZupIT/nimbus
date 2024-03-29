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

package br.com.zup.nimbus.core

import br.com.zup.nimbus.core.dependency.CommonDependency
import br.com.zup.nimbus.core.tree.ServerDrivenAction
import br.com.zup.nimbus.core.tree.ServerDrivenEvent

interface ActionEvent {
  /**
   * The action of this event. Use this object to find the name of the action, its properties and metadata.
   */
  val action: ServerDrivenAction
  /**
   * The scope of the event that triggered this ActionEvent.
   */
  val scope: ServerDrivenEvent
}

/**
 * All information needed for an action to execute. Represents the trigger event of an action.
 */
class ActionTriggeredEvent(
  override val action: ServerDrivenAction,
  override val scope: ServerDrivenEvent,
  /**
   * Every event can update the current state of the application based on the dependency graph. This set starts empty
   * when a ServerDrivenEvent is run. A ServerDrivenEvent is what triggers ActionEvents. Use this to tell the
   * ServerDrivenEvent (parent) which dependencies might need to propagate its changes to its dependents after it
   * finishes running. "Might" because it will still check if the dependency has really changed since the last time its
   * dependents were updated.
   */
  val dependencies: MutableSet<CommonDependency>,
): ActionEvent

/**
 * All information needed for an action to initialize. Represents the initialization event of an action.
 */
class ActionInitializedEvent(
  override val action: ServerDrivenAction,
  override val scope: ServerDrivenEvent,
): ActionEvent
