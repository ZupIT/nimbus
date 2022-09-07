package com.zup.nimbus.core.tree.builder

import com.zup.nimbus.core.ServerDrivenView
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
    view: ServerDrivenView,
    actionBuilder: ActionBuilder,
  ): ServerDrivenEvent {
    try {
      jsonEvent as List<Map<String, Any?>>
      val event = ServerDrivenEvent(name, parent, view)
      event.actions = jsonEvent.map { actionBuilder.fromJsonAction(it, event) }
      return event
    } catch (e: UnexpectedDataTypeError) {
      throw MalformedActionListError(e.message)
    }
  }
}
