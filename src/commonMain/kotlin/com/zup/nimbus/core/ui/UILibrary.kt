package com.zup.nimbus.core.ui

import com.zup.nimbus.core.ActionHandler
import com.zup.nimbus.core.ActionInitializationHandler
import com.zup.nimbus.core.OperationHandler

class UILibrary(val namespace: String = "") {
  private val actions = mutableMapOf<String, ActionHandler>()
  private val actionInitializers = mutableMapOf<String, ActionInitializationHandler>()
  private val actionObservers = mutableListOf<ActionHandler>()
  private val operations = mutableMapOf<String, OperationHandler>()

  fun addAction(name: String, handler: ActionHandler): UILibrary {
    actions[name] = handler
    return this
  }

  fun addActionInitializer(name: String, handler: ActionInitializationHandler): UILibrary {
    actionInitializers[name] = handler
    return this
  }

  fun addActionObserver(observer: ActionHandler): UILibrary {
    actionObservers.add(observer)
    return this
  }

  fun addOperation(name: String, handler: OperationHandler): UILibrary {
    operations[name] = handler
    return this
  }

  fun getAction(name: String): ActionHandler? {
    return actions[name]
  }

  fun getActionInitializer(name: String): ActionInitializationHandler? {
    return actionInitializers[name]
  }

  fun getActionObservers(): List<ActionHandler> {
    return actionObservers
  }

  fun getOperation(name: String): OperationHandler? {
    return operations[name]
  }

  fun merge(other: UILibrary) {
    actions.putAll(other.actions)
    actionInitializers.putAll(other.actionInitializers)
    actionObservers.addAll(other.actionObservers)
    operations.putAll(other.operations)
  }
}
