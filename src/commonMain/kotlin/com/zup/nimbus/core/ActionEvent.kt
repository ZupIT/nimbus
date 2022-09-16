package com.zup.nimbus.core

import com.zup.nimbus.core.dependency.Dependency
import com.zup.nimbus.core.tree.ServerDrivenAction
import com.zup.nimbus.core.tree.ServerDrivenEvent

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
  val dependencies: MutableSet<Dependency>,
): ActionEvent

class ActionInitializedEvent(
  override val action: ServerDrivenAction,
  override val scope: ServerDrivenEvent,
): ActionEvent
