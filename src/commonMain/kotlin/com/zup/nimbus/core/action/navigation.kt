package com.zup.nimbus.core.action

import com.zup.nimbus.core.render.ActionTriggeredEvent

fun pushView(event: ActionTriggeredEvent) {
  if (event.action.properties == null || event.action.properties["url"] !is String) {
    return event.view.nimbusInstance.logger.error("Push view action has no URL.")
  }
  event.view.parentNavigator.push(event.action.properties["url"] as String)
}

fun popView(event: ActionTriggeredEvent) {
  event.view.parentNavigator.pop()
}
