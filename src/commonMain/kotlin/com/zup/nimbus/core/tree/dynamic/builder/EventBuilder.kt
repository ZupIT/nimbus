package com.zup.nimbus.core.tree.dynamic.builder

import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.tree.dynamic.DynamicEvent
import com.zup.nimbus.core.utils.UnexpectedDataTypeError

/**
 * Builds DynamicEvents from JSON sources.
 */
class EventBuilder(nimbus: Nimbus) {
  private val actionBuilder = ActionBuilder(nimbus)

  /**
   * Verifies if this JSON structure represents an event. An event is a list of actions.
   */
  fun isJsonEvent(maybeEvent: Any?): Boolean {
    return maybeEvent is List<*> && maybeEvent.isNotEmpty() && actionBuilder.isJsonAction(maybeEvent.first()!!)
  }

  /**
   * Builds a DynamicEvent from its json map representation.
   *
   * To avoid errors, verify if `map` is an event (isJsonEvent) before calling this method.
   */
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
