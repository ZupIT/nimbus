package com.zup.nimbus.core.tree.builder

import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.tree.DynamicEvent
import com.zup.nimbus.core.tree.MalformedActionListError
import com.zup.nimbus.core.tree.ServerDrivenEvent
import com.zup.nimbus.core.utils.UnexpectedDataTypeError

class EventBuilder(nimbus: Nimbus) {
  private val actionBuilder = ActionBuilder(nimbus)

  fun isJsonEvent(maybeEvent: Any?): Boolean {
    return maybeEvent is List<*> && maybeEvent.isNotEmpty() && actionBuilder.isJsonAction(maybeEvent.first()!!)
  }

  fun buildFromJsonMap(name: String, jsonEvent: Any?): DynamicEvent {
    try {
      @Suppress("UNCHECKED_CAST")
      jsonEvent as List<Map<String, Any?>>
      val event = DynamicEvent(name)
      event.actions = jsonEvent.map { actionBuilder.buildFromJsonMap(it) }
      return event
    } catch (e: UnexpectedDataTypeError) {
      throw MalformedActionListError(e.message)
    }
  }
}
