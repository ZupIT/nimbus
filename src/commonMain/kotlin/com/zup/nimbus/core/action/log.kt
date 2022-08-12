package com.zup.nimbus.core.action

import com.zup.nimbus.core.log.LogLevel
import com.zup.nimbus.core.render.ActionEvent
import com.zup.nimbus.core.utils.UnexpectedDataTypeError
import com.zup.nimbus.core.utils.valueOfEnum
import com.zup.nimbus.core.utils.valueOfKey

internal fun log(event: ActionEvent) {
  val logger = event.view.nimbusInstance.logger
  val properties = event.action.properties
  try {
    val message: String = valueOfKey(properties, "message")
    val level: LogLevel = valueOfEnum(properties, "level", LogLevel.Info)
    logger.log(message, level)
  } catch (e: UnexpectedDataTypeError) {
    logger.error("Error while attempting to log.\n${e.message}")
  }
}
