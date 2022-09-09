package com.zup.nimbus.core.tree.builder

import com.zup.nimbus.core.scope.ViewScope
import com.zup.nimbus.core.tree.MalformedActionListError
import com.zup.nimbus.core.tree.stateful.ServerDrivenEvent
import com.zup.nimbus.core.tree.stateful.Stateful
import com.zup.nimbus.core.utils.UnexpectedDataTypeError

object EventBuilder {
  fun isJsonEvent(maybeEvent: Any?): Boolean {
    return maybeEvent is List<*> && maybeEvent.isNotEmpty() && ActionBuilder.isJsonAction(maybeEvent.first()!!)
  }

  fun buildFromJsonEvent(
    name: String,
    jsonEvent: Any?,
    parent: Stateful,
    scope: ViewScope,
  ): ServerDrivenEvent {
    try {
      jsonEvent as List<Map<String, Any?>>
      val event = ServerDrivenEvent(name, parent, scope)
      event.actions = jsonEvent.map { ActionBuilder.fromJsonAction(it, event.scope) }
      return event
    } catch (e: UnexpectedDataTypeError) {
      throw MalformedActionListError(e.message)
    }
  }
}
