package com.zup.nimbus.core

typealias RawJsonMap = Map<String, Any?>
typealias ActionHandler = (event: ActionTriggeredEvent) -> Unit
typealias ActionInitializedHandler = (event: ActionInitializedEvent) -> Unit
typealias OperationHandler = (arguments: List<Any?>) -> Any?
