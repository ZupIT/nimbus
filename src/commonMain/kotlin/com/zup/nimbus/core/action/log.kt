package com.zup.nimbus.core.action

import com.zup.nimbus.core.log.LogLevel
import com.zup.nimbus.core.render.ActionTriggeredEvent
import com.zup.nimbus.core.utils.UnexpectedDataTypeError
import com.zup.nimbus.core.utils.valueOf
import com.zup.nimbus.core.utils.valueOfEnum

fun log(event: ActionTriggeredEvent) {
  val logger = event.view.nimbusInstance.logger
  val properties = event.action.properties
  try {
    val message: String = valueOf(properties, "message")
    val level: LogLevel = valueOfEnum(properties, "level", LogLevel.Info)
    logger.log(message, level)
  } catch (e: UnexpectedDataTypeError) {
    logger.error("Error while attempting to log.\n${e.message}")
  }
}
