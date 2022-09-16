package com.zup.nimbus.core

typealias ActionHandler = (event: ActionTriggeredEvent) -> Unit
typealias ActionInitializationHandler = (event: ActionInitializedEvent) -> Unit
typealias OperationHandler = (arguments: List<Any?>) -> Any?
