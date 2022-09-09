package com.zup.nimbus.core

typealias RawJsonMap = Map<String, Any?>
typealias ActionHandler = (event: ActionTriggeredEvent) -> Unit
typealias ActionInitializationHandler = (event: ActionInitializedEvent) -> Unit
typealias OperationHandler = (arguments: List<Any?>) -> Any?
